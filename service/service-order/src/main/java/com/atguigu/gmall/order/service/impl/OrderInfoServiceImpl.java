package com.atguigu.gmall.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.cart.feign.CartInfoFeign;
import com.atguigu.gmall.model.base.BaseEntity;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.util.OrderThreadLocalUtil;
import com.atguigu.gmall.product.feign.ItemFeign;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Date:2022/8/10
 * Author:cjc
 * Description:普通订单相关接口实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OrderInfoServiceImpl implements OrderInfoService {
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Autowired
    private CartInfoFeign cartInfoFeign;
    @Autowired
    private ItemFeign itemFeign;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    /**
     * 新增订单
     *
     * @param orderInfo
     */
    @Override
    public void addOrder(OrderInfo orderInfo) {
        //校验
        if (orderInfo == null){
            throw new RuntimeException("参数错误,新增订单失败");
        }
        //使用用户名自增加一
        Long increment = redisTemplate.opsForValue().increment("user_add_order_" + OrderThreadLocalUtil.get(), 1);
        //设置key过期时效
        redisTemplate.expire("user_add_order_" + OrderThreadLocalUtil.get(),10, TimeUnit.SECONDS);

        if (increment > 1){
            throw new RuntimeException("新增订单失败,重复提交订单");
        }
        try {
            //获取购物车信息
            Map<String, Object> cartInfo = cartInfoFeign.getOrderComfirm();
            //判断购物车是否为空
            if (cartInfo.isEmpty()){
                throw new RuntimeException("购物车数据为空,新增订单失败");
            }
            //补全订单信息
            //总金额
            orderInfo.setTotalAmount(new BigDecimal(cartInfo.get("totalMoney").toString()));
            //订单状态
            orderInfo.setOrderStatus(OrderStatus.UNPAID.getComment());
            //用户id
            orderInfo.setUserId(OrderThreadLocalUtil.get());
            //创建时间
            orderInfo.setCreateTime(new Date());
            //失效时间
            orderInfo.setExpireTime(new Date(System.currentTimeMillis()+1800000));
            //进度状态
            orderInfo.setProcessStatus(ProcessStatus.UNPAID.getComment());
            //新增订单
            int insert = orderInfoMapper.insert(orderInfo);
            if (insert <= 0){
                throw new RuntimeException("新增订单失败");
            }
            //订单生成成功获取订单id
            Long orderInfoId = orderInfo.getId();
            //获取订单详情数据
            List cartInfoList = (List) cartInfo.get("cartInfoList");
            //新增订单详情,获取商品应该扣除的库存信息
            Map<String, Object> skuParam = saveOrderDetail(orderInfoId, cartInfoList);
            //扣减库存
            itemFeign.reduceStock(skuParam);
            //清空购物车
            //cartInfoFeign.removeCart();
            //延时清除未支付订单
            rabbitTemplate.convertAndSend("order_normal_exchange",
                    "order.dead",
                    orderInfoId+"",
                    (message)->{
                        //获取消息属性
                        MessageProperties messageProperties = message.getMessageProperties();
                        //设置延时时间 测试设置的10秒过期时间
                        messageProperties.setExpiration(10000+"");
                        return message;
                    });
        } catch (RuntimeException e) {
            //代码出现异常
            throw new RuntimeException("新增订单失败");
        } finally {
            //清理标识为
            redisTemplate.delete("user_add_order_" + OrderThreadLocalUtil.get());
        }
    }

    /**
     * 取消订单
     *
     * @param orderId
     */
    @Override
    public void cancelOrder(Long orderId) {
        // 关闭交易 --todo 同步
        //获取用户名
        String username = OrderThreadLocalUtil.get();
        //幂等性 只处理未支付订单
        OrderInfo orderInfo = orderInfoMapper.selectOne(new LambdaQueryWrapper<OrderInfo>()
                .eq(BaseEntity::getId, orderId)
                .eq(OrderInfo::getOrderStatus, OrderStatus.UNPAID.getComment()));
        if (orderInfo == null || orderInfo.getId() == null){
            throw new RuntimeException("取消订单失败");
        }
        //判断是否为空
        if (StringUtils.isEmpty(username)){
            //为空说明是超时调用
            orderInfo.setOrderStatus(OrderStatus.TIMEOUT.getComment());
            orderInfo.setProcessStatus(OrderStatus.TIMEOUT.getComment());
        }else{
            //不为空说明用户手动调用
            orderInfo.setOrderStatus(OrderStatus.CANCEL.getComment());
            orderInfo.setProcessStatus(OrderStatus.CANCEL.getComment());
        }
        //修改订单
        int update = orderInfoMapper.updateById(orderInfo);
        if (update <= 0){
            throw new RuntimeException("取消订单失败");
        }
        //回滚库存根据订单详情
        rollbackStock(orderId);

    }

    /**
     * 回滚库存根据订单详情
     * @param orderId
     */
    private void rollbackStock(Long orderId) {
        //查询订单详情数据
        List<OrderDetail> orderDetailList = orderDetailMapper.selectList(new LambdaQueryWrapper<OrderDetail>()
                .eq(OrderDetail::getOrderId, orderId));
        //统计回滚数量
        ConcurrentHashMap<String, Object> skuParam = new ConcurrentHashMap<>();
        //遍历取值
        orderDetailList.stream().forEach(orderDetail -> {
            //获取回滚商品id
            Long skuId = orderDetail.getSkuId();
            //获取回滚数量
            Integer skuNum = orderDetail.getSkuNum();
            //保存
            skuParam.put(skuId+"",skuNum);
        });
        //调用回滚--
        itemFeign.rollbackStock(skuParam);
    }

    @Resource
    private OrderDetailMapper orderDetailMapper;

    /**
     * 新增订单详情
     * @param orderInfoId
     * @param cartInfoList
     * @return
     */
    private Map<String, Object> saveOrderDetail(Long orderInfoId, List cartInfoList) {
        //初始化
        Map<String, Object> map = new ConcurrentHashMap<>();
        //遍历购物车信息
        cartInfoList.stream().forEach(o->{
            //序列化
            String s = JSONObject.toJSONString(o);
            //反序列化
            CartInfo cartInfo = JSONObject.parseObject(s, CartInfo.class);
            //初始化
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderInfoId);
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setSkuName(cartInfo.getSkuName());
            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setOrderPrice(cartInfo.getSkuPrice().multiply(new BigDecimal(cartInfo.getSkuNum())));
            orderDetail.setSkuNum(cartInfo.getSkuNum());
            map.put(cartInfo.getSkuId()+"",cartInfo.getSkuNum());
            int insert = orderDetailMapper.insert(orderDetail);
            if (insert <= 0){
                throw new RuntimeException("新增订单详情失败,请重试");
            }
        });
        return map;
    }

    /**
     * 修改订单状态
     *
     * @param result
     */
    @Override
    public void updateOrder(String result) {
        //支付结果反序列
        Map<String, String> resultMap = JSONObject.parseObject(result, Map.class);
        //获取订单号
        String orderId = resultMap.get("out_trade_no");
        //查询订单信息
        OrderInfo orderInfo = orderInfoMapper.selectOne(new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getId, Long.parseLong(orderId))
                .eq(OrderInfo::getOrderStatus, OrderStatus.UNPAID.getComment()));
        if (orderInfo == null || orderInfo.getId() ==  null){
            return;
        }
        //存储第三方报文
        orderInfo.setTradeBody(result);
        //订单状态
        orderInfo.setOrderStatus(OrderStatus.PAID.getComment());
        orderInfo.setProcessStatus(OrderStatus.PAID.getComment());
        //判断支付渠道
        if (resultMap.get("payway").equals("1")){
            //微信
            orderInfo.setOutTradeNo(resultMap.get("transaction_id"));
        }else{
            //支付宝
            orderInfo.setOrderStatus(resultMap.get("trade_no"));
        }
        //修改
        int update = orderInfoMapper.updateById(orderInfo);
        if (update <= 0){
            throw new RuntimeException("修改支付订单支付结果失败");
        }
        //实战 仓库管理系统 --todo
    }
}
