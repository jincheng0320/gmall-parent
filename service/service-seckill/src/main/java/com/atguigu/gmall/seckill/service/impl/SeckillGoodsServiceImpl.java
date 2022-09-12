package com.atguigu.gmall.seckill.service.impl;

import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.seckill.mapper.SeckillGoodsMapper;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * Date:2022/8/15
 * Author:cjc
 * Description:秒杀商品接口实现类
 */
@Service
@Log4j2
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据指定的时间段查询商品列表
     *
     * @param time
     * @return
     */
    @Override
    public List<SeckillGoods> getSeckillGoods(String time) {
        return redisTemplate.opsForHash().values(time);
    }

    /**
     * 查询指定的秒杀商品
     *
     * @param time
     * @param goodsId
     * @return
     */
    @Override
    public SeckillGoods getSeckillGoods(String time, String goodsId) {
        return (SeckillGoods) redisTemplate.opsForHash().get(time,goodsId);
    }

    @Resource
    private SeckillGoodsMapper seckillGoodsMapper;
    /**
     * 同步商品数据
     *
     * @param key
     */
    @Override
    public void mergeSeckillGoodsStockToDb(String key) {
        //从redis中获取数据
        Set seckillGoodsId = redisTemplate.opsForHash().keys("SeckillGoodsStockCount" + key);
        if (seckillGoodsId != null && seckillGoodsId.size() > 0){
            //遍历tbu1
            seckillGoodsId.stream().forEach(goodsId->{
                try {
                    //获取商品剩余库存
                    Integer stock = (Integer) redisTemplate.opsForHash().get("SeckillGoodsStockCount" + key, goodsId);
                    //同步数据库
                    int i = seckillGoodsMapper.updateStock(Long.parseLong(goodsId.toString()), stock);
                    if (i < 0){
                        log.error("商品库存同步失败,商品id为 :"+goodsId);
                    }
                    //同步成功将redis中这条数据删除
                    redisTemplate.opsForHash().delete("SeckillGoodsStockCount" + key, goodsId);
                } catch (NumberFormatException e) {
                    log.error("商品库存同步失败,商品id为 :"+goodsId);
                }
            });

        }
    }
}
