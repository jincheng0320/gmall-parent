package com.atguigu.gmall.cart.service;


import com.atguigu.gmall.model.cart.CartInfo;

import java.util.List;
import java.util.Map;

/**
 * Date:2022/8/9
 * Author:cjc
 * Description:购物车相关接口
 */
public interface CartInfoService {

    /**
     * 添加购物车
     * @param skuId
     * @param num
     */
    public void addCartInfo(Long skuId, Integer num);

    /**
     * 显示购物车列表
     */
    public List<CartInfo> showCart();

    /**
     * 删除购物车
     */
    public void removeCart(Long cartId);

    /**
     * 更改选择状态
     */
    public void checkOrUncheck(Long cartId, Short status);

    /**
     * 获取用户本次要购买的信息
     * @return
     */
    public Map<String, Object> getOrderComfirm();

    /**
     * 清空购物车
     */
    public void clearCart();
}
