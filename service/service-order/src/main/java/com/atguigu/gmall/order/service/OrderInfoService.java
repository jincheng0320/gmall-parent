package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;

/**
 * Date:2022/8/10
 * Author:cjc
 * Description:普通订单相关的接口
 */
public interface OrderInfoService {

    /**
     * 新增订单
     */
    public void addOrder(OrderInfo orderInfo);

    /**
     * 取消订单
     * @param orderId
     */
    public void cancelOrder(Long orderId);

    /**
     * 修改订单状态
     * @param result
     */
    public void updateOrder(String result);
}
