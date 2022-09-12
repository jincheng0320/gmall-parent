package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.service.OrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * Date:2022/8/10
 * Author:cjc
 * Description:订单控制层
 */
@RestController
@RequestMapping("/api/order")
public class OrderInfoController {
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 新增订单
     * @param orderInfo
     * @return
     */
    @GetMapping("/addOrder")
    public Result addOrder(@RequestBody OrderInfo orderInfo){
        orderInfoService.addOrder(orderInfo);
        return Result.ok();
    }

    /**
     * 主动取消订单
     * @param orderId
     * @return
     */
    @GetMapping("/cancelOrder")
    public Result cancelOrder(Long orderId){
        //避免重复发起取消订单
        Long increment = redisTemplate.opsForValue().increment("user_cancle_order_count_" + orderId, 1);
        redisTemplate.expire("user_cancle_order_count_" + orderId,5, TimeUnit.SECONDS);
        if (increment > 1){
            return Result.ok();
        }
        try {
            orderInfoService.cancelOrder(orderId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisTemplate.delete("user_cancle_order_count_" + orderId);
        }
        return Result.ok();
    }
}
