package com.atguigu.gmall.seckill.service;

import com.atguigu.gmall.seckill.pojo.UserRecode;

import java.util.concurrent.ExecutionException;

/**
 * Date:2022/8/15
 * Author:cjc
 * Description:秒杀商品订单接口
 */
public interface SeckillOrderService {

    /**
     * 秒杀下单 --伪下单,排队
     * @return
     */
    public UserRecode addSeckillOrder(String time, String goodsId, Integer num);

    /**
     * 查询用户排队状态
     * @return
     */
    public UserRecode getUserRecode();

    /**
     * 真实下单
     */
    public void realAddSeckillOrder(UserRecode userRecode) throws ExecutionException, InterruptedException;

    /**
     * 取消订单
     * @param username
     */
    public void cancelSeckillOrder(String username);

    /**
     * 修改秒杀订单的支付结果
     * @param result
     */
    public void updateSeckillOrder(String result);
}
