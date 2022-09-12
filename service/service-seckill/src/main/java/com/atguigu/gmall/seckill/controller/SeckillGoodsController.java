package com.atguigu.gmall.seckill.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import com.atguigu.gmall.seckill.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Date:2022/8/15
 * Author:cjc
 * Description:秒杀商品接口
 */
@RestController
@RequestMapping("/api/seckill/goods")
public class SeckillGoodsController {
    @Autowired
    private SeckillGoodsService seckillGoodsService;

    /**
     * 根据指定时间段查询商列表
     * @param time
     * @return
     */
    @GetMapping("/getSeckillGoods")
    public Result getSeckillGoods(String time){
        return Result.ok(seckillGoodsService.getSeckillGoods(time));
    }

    /**
     * 查询指定的秒杀商品
     * @param time
     * @param goodsId
     * @return
     */
    @GetMapping("/getSeckillGood")
    public Result getSeckillGood(String time, String goodsId){
        return Result.ok(seckillGoodsService.getSeckillGoods(time, goodsId));
    }

    /**
     * 获取时间段菜单
     * @return
     */
    @GetMapping("/getDateMenus")
    public Result getDateMenus(){
        return Result.ok(DateUtil.getDateMenus());
    }
}
