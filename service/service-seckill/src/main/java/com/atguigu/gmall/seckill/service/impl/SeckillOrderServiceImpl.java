package com.atguigu.gmall.seckill.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.seckill.mapper.SeckillOrderMapper;
import com.atguigu.gmall.seckill.pojo.SeckillOrder;
import com.atguigu.gmall.seckill.pojo.UserRecode;
import com.atguigu.gmall.seckill.service.SeckillOrderService;
import com.atguigu.gmall.seckill.util.DateUtil;
import com.atguigu.gmall.seckill.util.SeckillThreadLocalUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Date:2022/8/15
 * Author:cjc
 * Description:秒杀商品订单接口实现类
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    @Resource
    private SeckillOrderMapper seckillOrderMapper;
    /**
     * 秒杀下单 --伪下单,排队
     *
     * @param time
     * @param goodsId
     * @param num
     * @return
     */
    @Override
    public UserRecode addSeckillOrder(String time, String goodsId, Integer num) {
        String username = SeckillThreadLocalUtil.get();
        //排队 记录用户 买哪个商品 买几个
        UserRecode userRecode = new UserRecode();
        //确认用户只排一次队
        Long increment = redisTemplate.opsForValue().increment("User_Queue_Count_" + username, 1);
        //设置和订单有效期时间一致 , 订单有效期过即可再次下单
        redisTemplate.expire("User_Queue_Count_" + username, 300, TimeUnit.SECONDS);
        if (increment > 1){
            userRecode.setStatus(3);
            userRecode.setMsg("秒杀失败,请勿重复下单");
            return userRecode;
        }
        //排队数据补充
        userRecode.setUsername(username);
        userRecode.setCreateTime(new Date());
        userRecode.setStatus(1);
        userRecode.setGoodsId(goodsId);
        userRecode.setTime(time);
        userRecode.setNum(num);
        userRecode.setMsg("排队中!");
        //异步执行
        CompletableFuture.runAsync(()->{
            //将排队状态写入redis中
            redisTemplate.opsForValue().set("User_Recode_"+username, userRecode);
            //通知下单
            rabbitTemplate.convertAndSend("seckill_order_exchange","seckill.order", JSONObject.toJSONString(userRecode));
        }, threadPoolExecutor).whenCompleteAsync((a,b)->{
            if (b != null){
                userRecode.setStatus(3);
                userRecode.setMsg("秒杀下单失败,请重试!");
                redisTemplate.opsForValue().set("User_Recode"+username, userRecode);
            }
        }, threadPoolExecutor);
        return userRecode;
    }

    /**
     * 查询用户排队状态
     *
     * @return
     */
    @Override
    public UserRecode getUserRecode() {
        String username = SeckillThreadLocalUtil.get();
        return (UserRecode) redisTemplate.opsForValue().get("User_Recode_"+username);
    }

    /**
     * 真实下单
     *
     * @param userRecode
     */
    @Override
    public void realAddSeckillOrder(UserRecode userRecode) throws ExecutionException, InterruptedException {
        //获取商品的时间段
        String time = userRecode.getTime();
        //获取商品id
        String goodsId = userRecode.getGoodsId();
        //获取商品数量
        Integer num = userRecode.getNum();
        //获取用户名
        String username = userRecode.getUsername();
        //从redis中获取数据
        SeckillGoods seckillGoods =
                (SeckillGoods) redisTemplate.opsForHash().get(time, goodsId);
        //判断商是否存在
        if (seckillGoods == null || seckillGoods.getId() == null){
            addSeckillOrderFail(userRecode,"商品不存在");
            return;
        }

        //是否在活动范围内
        String nowTime =
                DateUtil.data2str(DateUtil.getDateMenus().get(0), DateUtil.PATTERN_YYYYMMDDHH);
        if (!nowTime.equals(time)){
            addSeckillOrderFail(userRecode,"商品不在活动范围内");
            return;
        }

        //商品是否超限购
        if (num <=0 || seckillGoods.getSeckillLimit() < num){
            addSeckillOrderFail(userRecode,"商品超出限购");
            return;
        }

        //扣减库存
        for (int i = 0; i < num; i++) {
            Object o = redisTemplate.opsForList().rightPop("Seckill_Goods_Stock_Queue" + goodsId);
            if (o == null){
                addSeckillOrderFail(userRecode,"商品库存不足");
                //回滚库存
                if (i > 0){
                    redisTemplate.opsForList().leftPushAll("Seckill_Goods_Stock_Queue"+seckillGoods.getId(),
                            getStack(i, goodsId));
                }
            }
        }

        try{
            //商品库存更新
            Long increment = redisTemplate.opsForHash().increment("SeckillGoodsStockCount" + time, goodsId, -num);
            seckillGoods.setStockCount(increment.intValue());
            redisTemplate.opsForHash().put(time, goodsId, seckillGoods);
            //下单
            SeckillOrder seckillOrder = new SeckillOrder();
            seckillOrder.setId(UUID.randomUUID().toString().replace("-",""));
            seckillOrder.setGoodsId(goodsId);
            seckillOrder.setNum(num);
            seckillOrder.setMoney(seckillGoods.getCostPrice().multiply(new BigDecimal(num)).toString());
            seckillOrder.setUserId(username);
            seckillOrder.setCreateTime(new Date());
            seckillOrder.setStatus("0");
            //异步写数据库
            CompletableFuture<Boolean> future1 = CompletableFuture.supplyAsync(() -> {
                seckillOrderMapper.insert(seckillOrder);
                return true;
            }, threadPoolExecutor).exceptionally((a) -> {
                return false;
            });
            //异步写redis
            CompletableFuture<Boolean> future2 = CompletableFuture.supplyAsync(() -> {
                redisTemplate.opsForHash().put("User_Seckill_Order_" + time, seckillOrder.getId(), seckillGoods);
                return true;
            }, threadPoolExecutor).exceptionally((a) -> {
                return false;
            });
            if (!future1.get() && !future2.get()){
               throw new RuntimeException("写库redis同时失败");
            }
            //补全订单号和金额到排队状态中
            userRecode.setMoney(seckillOrder.getMoney());
            userRecode.setOrderId(seckillOrder.getId());
            userRecode.setStatus(2);
            userRecode.setMsg("秒杀成功,等待支付");
            //更新redis排队状态
            redisTemplate.opsForValue().set("User_Recode_"+username,userRecode);
            //订单的延时后续处理: 1.取消 2.超时 3.支付
            rabbitTemplate.convertAndSend("seckill_order_nomal_exchange",
                    "seckill.order.dead",
                    username,
                    message -> {
                        MessageProperties messageProperties = message.getMessageProperties();
                        messageProperties.setExpiration("60000");
                        return message;
                    });
        }catch (Exception e){
            //商品不存在 秒杀失败
            userRecode.setStatus(3);
            userRecode.setMsg("秒杀失败!请重试");
            //修改用户排队状态
            redisTemplate.opsForValue().set("User_Recode_"+username, userRecode);
            //删除排队计数器
            redisTemplate.delete("User_Queue_Count_" + username);
            //下单若失败 回滚库存
            redisTemplate.opsForList().leftPush("Seckill_Goods_Stock_Queue"+goodsId, goodsId);
        }
    }

    /**
     * 取消订单
     *
     * @param username
     */
    @Override
    public void cancelSeckillOrder(String username) {
        String msg = "";
        if (!StringUtils.isEmpty(username)){
            //超时取消
            msg="超时取消";
        }else {
            msg="主动取消";
        }
        //获取用户排队状态
        UserRecode userRecode = (UserRecode) redisTemplate.opsForValue().get("User_Recode_" + username);
        //查询订单- 数据库中订单数据
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(new LambdaQueryWrapper<SeckillOrder>()
                .eq(SeckillOrder::getUserId, username)
                .eq(SeckillOrder::getStatus, "0"));
        //判断
        if (seckillOrder == null || seckillOrder.getId() == null){
            //数据库没有去redis中查
            seckillOrder = (SeckillOrder) redisTemplate.opsForHash().get("User_Seckill_Order_" + userRecode.getTime(),
                    userRecode.getOrderId());
            if (seckillOrder == null || StringUtils.isEmpty(seckillOrder.getId())){
                return;
            }
        }
        //修改订单
        seckillOrder.setStatus(msg);
        int update = seckillOrderMapper.updateById(seckillOrder);
        if (update <= 0){
            throw new RuntimeException("取消订单失败,订单号为:"+seckillOrder.getId());
        }
        //回滚库存
        rollbackSeckillGoodsStock(userRecode);
        //清理标识位-排队计数器
        redisTemplate.delete("User_Queue_Count_" + seckillOrder.getUserId());
        //排队状态-删除
        redisTemplate.delete("User_Recode_"+seckillOrder.getUserId());
        //删除redis临时订单
        redisTemplate.opsForHash().delete("User_Seckill_Order_" + userRecode.getTime(), seckillOrder.getId());
    }

    /**
     * 修改秒杀订单的支付结果
     *
     * @param result
     */
    @Override
    public void updateSeckillOrder(String result) {
        //支付结果反序列化
        Map<String, String> resultMap = JSONObject.parseObject(result, Map.class);
        //获取订单号
        String orderId = resultMap.get("out_trade_no");
        //查询订单信息
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(new LambdaQueryWrapper<SeckillOrder>()
                .eq(SeckillOrder::getId, orderId)
                .eq(SeckillOrder::getStatus, "0"));
        //获取附加参数
        String attach = resultMap.get("attach");
        Map<String, String> attachMap = JSONObject.parseObject(attach, Map.class);
        String username = attachMap.get("username");
        //获取用户排队状态
        UserRecode userRecode = (UserRecode) redisTemplate.opsForValue().get("User_Recode_" + username);
        if (seckillOrder == null || seckillOrder.getId() == null){
            //数据库没有找redis
            seckillOrder =(SeckillOrder) redisTemplate.opsForHash().get("User_Seckill_Order_" + userRecode.getTime(),
                    userRecode.getOrderId());
            if (seckillOrder == null || StringUtils.isEmpty(seckillOrder.getId())){
                return;
            }
        }
        //修改订单状态
        seckillOrder.setStatus("已支付");
        //判断支付渠道
        if (resultMap.get("payway").equals("1")){
            //微信流水号
            seckillOrder.setOutTradeNo(resultMap.get("transaction_id"));
        }else {
            //支付宝流水号
            seckillOrder.setOutTradeNo(resultMap.get("trade_no"));
        }
        //修改
        int update = seckillOrderMapper.updateById(seckillOrder);
        if (update <= 0){
            throw new RuntimeException("修改订单支付结果失败");
        }
        //标识位没清理--排队计数器删除
        redisTemplate.delete("User_Queue_Count_" + seckillOrder.getUserId());
        //排队状态--删除掉
        redisTemplate.delete("User_Recode_" + seckillOrder.getUserId());
        //删除redis中的临时订单数据
        redisTemplate.opsForHash().delete("User_Seckill_Order_" + userRecode.getTime(), seckillOrder.getId());
    }

    /**
     * 回滚库存
     * @param userRecode
     */
    private void rollbackSeckillGoodsStock(UserRecode userRecode) {
        //回滚库存自增值  获取回滚数量
        Long increment = redisTemplate.opsForHash().increment("SeckillGoodsStockCount" + userRecode.getTime(),
                userRecode.getGoodsId(),
                userRecode.getNum());
        //从redis中获取商品
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.opsForHash().get(userRecode.getTime(), userRecode.getGoodsId());
        if (seckillGoods != null){
            //活动没有结束
            seckillGoods.setStockCount(increment.intValue());
            redisTemplate.opsForHash().put(userRecode.getTime(), userRecode.getGoodsId(), seckillGoods);
            //计算队列需要回滚数量
            String[] stack = getStack(userRecode.getNum(), userRecode.getGoodsId());
            redisTemplate.opsForList().leftPushAll("Seckill_Goods_Stock_Queue"+userRecode.getGoodsId(), stack);
        }
    }

    /**
     * 创建存在库存数量数组
     * @param stockCount
     * @param goodsId
     * @return
     */
    private String[] getStack(Integer stockCount, String goodsId) {
        //创建存在库存的数组
        String[] strings = new String[stockCount];
        for (int i = 0; i < stockCount; i++) {
            strings[i] = goodsId;
        }
        return strings;
    }

    /**
     * 下单失败处理
     * @param userRecode
     * @param mes
     */
    private void addSeckillOrderFail(UserRecode userRecode, String mes){
        //商品不存在 秒杀失败
        userRecode.setStatus(3);
        userRecode.setMsg(mes+"秒杀失败!");
        //修改redis用户排队状态
        redisTemplate.opsForValue().set("User_Recode_"+userRecode.getUsername(), userRecode);
        //删除排队计数器不能影响用户购买其他商品
        redisTemplate.delete("User_Queue_Count_" + userRecode.getUsername());
    }
}
