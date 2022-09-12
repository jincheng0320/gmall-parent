package com.atguigu.gmall.seckill.task;

import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.base.BaseEntity;
import com.atguigu.gmall.seckill.mapper.SeckillGoodsMapper;
import com.atguigu.gmall.seckill.util.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Date:2022/8/15
 * Author:cjc
 * Description:秒杀商品从数据库定时写到redis
 */
@Component
public class SeckillGoodsFromDbToRedis {

    @Resource
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Scheduled(cron = "1/20 * * * * *")
    public void seckillGoodsFromDbToRedis(){
        //获取当前时间及后面4个时间段
        List<Date> dateMenus = DateUtil.getDateMenus();
        //遍历
        dateMenus.stream().forEach(start->{
            //获取开始时间
            String startTime = DateUtil.data2str(start, DateUtil.PATTERN_YYYY_MM_DDHHMM);
            //获取结束时间
            String endTime = DateUtil.data2str(DateUtil.addDateHour(start, 2), DateUtil.PATTERN_YYYY_MM_DDHHMM);
            //结束时间
            Date end = DateUtil.addDateHour(start, 2);
            //计算redis存活时间
            long liveTime = end.getTime()-System.currentTimeMillis();
            //将开始时间存为大key 格式为2022081516
            String key = DateUtil.data2str(start, DateUtil.PATTERN_YYYYMMDDHH);
            //拼接查询条件
            LambdaQueryWrapper<SeckillGoods> wrapper = new LambdaQueryWrapper<>();
            //审核状态
            wrapper.eq(SeckillGoods::getStatus, 1);
            //大于等于活动开始时间
            wrapper.ge(SeckillGoods::getStartTime, start);
            //小于结束时间
            wrapper.le(SeckillGoods::getEndTime, endTime);
            //库存数量大于0
            wrapper.gt(SeckillGoods::getStockCount, 0);
            //redis中没有的商品
            Set keys = redisTemplate.opsForHash().keys(key);
            if (keys != null && keys.size() > 0){
                wrapper.notIn(BaseEntity::getId, keys);
            }
            //查询商品列表
            List<SeckillGoods> seckillGoodsList = seckillGoodsMapper.selectList(wrapper);
            //遍历
            seckillGoodsList.stream().forEach(seckillGoods -> {
                //将商品列表存到redis中
                redisTemplate.opsForHash().put(key, seckillGoods.getId()+"", seckillGoods);
                //创建一个库存数量长度的数组
                String[] stack = getStack(seckillGoods.getStockCount(), seckillGoods.getId()+"");
                //将库存存入redis中
                redisTemplate.opsForList().leftPushAll("Seckill_Goods_Stock_Queue"+seckillGoods.getId(), stack);
                redisTemplate.expire("Seckill_Goods_Stock_Queue"+seckillGoods.getId(),liveTime, TimeUnit.MILLISECONDS);
                //构建商品自增值,统计剩余库存
                redisTemplate.opsForHash().increment("SeckillGoodsStockCount"+key, seckillGoods.getId()+"", seckillGoods.getStockCount());
            });
            //设置商品数据过期时间
            setSeckillGoodsExpire(liveTime, key);
        });
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 设置商品数据过期时间
     * @param liveTime
     * @param key
     */
    private void setSeckillGoodsExpire(long liveTime, String key) {
        //确认每个时间段设置过期时间一次
        Long increment = redisTemplate.opsForHash().increment("SeckillGoodsExpireTimes", key, 1);
        if (increment > 1){
            return;
        }
        //设置商品数据过期时间
        redisTemplate.expire(key, liveTime, TimeUnit.MILLISECONDS);
        //发送延时消息 触发数据同步
        rabbitTemplate.convertAndSend("seckill_goods_nomal_exchange",
                "seckill.goods.dead",
                key,
                message -> {
                    MessageProperties messageProperties = message.getMessageProperties();
                    //设置延时时间
                    messageProperties.setExpiration((liveTime+60000)+"");
                    //messageProperties.setExpiration(30000+"");
                    //返回
                    return message;
                });
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
}
