package com.atguigu.gmall.seckill.service;

import com.atguigu.gmall.model.activity.SeckillGoods;

import java.util.List;

/**
 * Date:2022/8/15
 * Author:cjc
 * Description:秒杀商品的接口
 */
public interface SeckillGoodsService {

    /**
     * 根据指定的时间段查询秒杀商品列表
     * @param time
     * @return
     */
    public List<SeckillGoods> getSeckillGoods(String time);

    /**
     * 查询指定的秒杀商品
     * @param time
     * @param goodsId
     * @return
     */
    public SeckillGoods getSeckillGoods(String time, String goodsId);

    /**
     * 同步商品数据
     * @param key
     */
    public void mergeSeckillGoodsStockToDb(String key);
}
