package com.atguigu.gmall.seckill.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.seckill.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Date:2022/8/15
 * Author:cjc
 * Description:秒杀商品订单
 */
@RestController
@RequestMapping("/api/seckill/order")
public class SeckillOrderController {
    @Autowired
    private SeckillOrderService seckillOrderService;

    /**
     * 秒杀下单
     * @param time
     * @param goodsId
     * @param num
     * @return
     */
    @GetMapping("/addSeckillOrder")
    public Result addSeckillOrder(String time, String goodsId, Integer num){
        return Result.ok(seckillOrderService.addSeckillOrder(time, goodsId, num));
    }

    /**
     * 查询排队状态
     * @return
     */
    @GetMapping("/getUserRecode")
    public Result getUserRecode(){
        return Result.ok(seckillOrderService.getUserRecode());
    }
}
