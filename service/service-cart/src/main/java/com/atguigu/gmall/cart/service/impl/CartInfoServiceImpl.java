package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.cart.service.CartInfoService;
import com.atguigu.gmall.cart.util.CartThreadLocalUtil;
import com.atguigu.gmall.common.constant.CartConst;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.feign.ItemFeign;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.util.concurrent.AtomicDouble;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Date:2022/8/9
 * Author:cjc
 * Description:购物车相关接口实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CartInfoServiceImpl implements CartInfoService {
    @Resource
    private CartInfoMapper cartInfoMapper;
    @Autowired
    private ItemFeign itemFeign;

    /**
     * 添加购物车
     * @param skuId
     * @param num
     */
    @Override
    public void addCartInfo(Long skuId, Integer num) {
        //参数校验
        if (skuId == null || num == null){
            throw new RuntimeException("参数错误.添加购物车失败");
        }
        //获取商品
        SkuInfo skuInfo = itemFeign.getSkuInfo(skuId);
        //商品校验
        if (skuInfo == null || skuInfo.getId() == null){
            return;
        }
        //获取用户名
        String username = CartThreadLocalUtil.get();
        //查询该用户购物车
        CartInfo cartInfo = cartInfoMapper.selectOne(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getUserId, username)
                .eq(CartInfo::getSkuId, skuId));
        //判断用户购物车该商品是否存在
        if (cartInfo == null || cartInfo.getId() == null){
            if (num <= 0){
                return;
            }
            //该用户购物没有此商品 进行添加
            //包装数据
            cartInfo=new CartInfo();
            cartInfo.setUserId(username);
            cartInfo.setSkuId(skuId);
            //获取价格
            BigDecimal prices = itemFeign.getPrices(skuId);
            cartInfo.setCartPrice(prices);
            //数量
            cartInfo.setSkuNum(num);
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo.setSkuName(skuInfo.getSkuName());
            //保存
            int insert = cartInfoMapper.insert(cartInfo);
            if (insert <= 0){
                throw new RuntimeException("添加购物车失败");
            }
        }else {
            //购物车有该商品
            num = cartInfo.getSkuNum()+num;
            if (num <= 0){
                int i = cartInfoMapper.deleteById(cartInfo.getId());
                if (i < 0){
                    throw new RuntimeException("添加购物车失败");
                }
            }
            //合并数量
            cartInfo.setSkuNum(num);
            //更新
            int update = cartInfoMapper.updateById(cartInfo);
            if (update < 0){
                throw new RuntimeException("添加购物车失败");
            }
        }

    }

    /**
     * 显示购物车
     *
     * @param
     * @return
     */
    @Override
    public List<CartInfo> showCart() {
        //获取用户名
        String username = CartThreadLocalUtil.get();
        return cartInfoMapper.selectList(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getUserId, username));
    }

    /**
     * 删除购物车
     *
     * @param
     */
    @Override
    public void removeCart(Long cartId) {
        //获取用户名
        String username = CartThreadLocalUtil.get();
        int delete = cartInfoMapper.delete(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getUserId, username)
                .eq(CartInfo::getId, cartId));
        //判断删除是否成功
        if (delete < 0){
            throw new RuntimeException("删除失败");
        }
    }

    /**
     * 更改选择状态
     * @param cartId
     * @param status
     */
    @Override
    public void checkOrUncheck(Long cartId, Short status) {
        //获取用户名
        String username = CartThreadLocalUtil.get();
        int i = 0;
        //判断修改全部还是单条修改
        if (cartId == null){
            //cartId为null则全部修改
            i = cartInfoMapper.updateAll(username, status);
        }else{
            //根据cartId选择状态
            i = cartInfoMapper.updateOne(username, cartId, status);
        }
        if (i < 0){
            throw new RuntimeException("修改选择状态失败");
        }
    }

    /**
     * 获取用户本次要购买的信息
     *
     * @return
     */
    @Override
    public Map<String, Object> getOrderComfirm() {
        //先跟用户名查询购物车勾选信息
        List<CartInfo> cartInfoList = cartInfoMapper.selectList(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getUserId, CartThreadLocalUtil.get())
                .eq(CartInfo::getIsChecked, CartConst.CART_CHECK));
        //不为空说明购物车有数据
        if (!cartInfoList.isEmpty()){
            //返回结果初始化
            Map<String, Object> map = new HashMap<>();
            //总数量
            AtomicInteger skuNum = new AtomicInteger(0);
            //总金额
            AtomicDouble totalMoney = new AtomicDouble(0);
            //遍历每个购物车商品 查询实时价格
            List<CartInfo> cartInfoListNew = cartInfoList.stream().map(cartInfo -> {
                //获取实时价格
                BigDecimal prices = itemFeign.getPrices(cartInfo.getSkuId());
                //保存实时价格
                cartInfo.setSkuPrice(prices);
                //获取购物车商品数量
                skuNum.getAndAdd(cartInfo.getSkuNum());
                //获取商品总金额
                totalMoney.getAndAdd(prices.doubleValue() * cartInfo.getSkuNum());
                return cartInfo;
            }).collect(Collectors.toList());
            //保存总数量
            map.put("skuNum", skuNum);
            //保存总金额
            map.put("totalMoney", totalMoney);
            //保存购物车列表
            map.put("cartInfoList",cartInfoListNew);
            return map;
        }
        return null;
    }

    /**
     * 清空购物车
     */
    @Override
    public void clearCart() {
        int delete = cartInfoMapper.delete(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getUserId, CartThreadLocalUtil.get())
                .eq(CartInfo::getIsChecked, CartConst.CART_CHECK));
        if (delete < 0){
            throw new RuntimeException("清除购物车失败");
        }
    }
}
