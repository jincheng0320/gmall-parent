package com.atguigu.gmall.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Date:2022/8/16
 * Author:cjc
 * Description:秒杀商品活动结束数据同步消息队列配置
 */
@Configuration
public class SeckillGoodsRabbitConfig {

    /**
     * 正常交换机
     * @return
     */
    @Bean("seckillGoodsNomalExchange")
    public Exchange seckillGoodsNomalExchange(){
        return ExchangeBuilder.directExchange("seckill_goods_nomal_exchange").build();
    }

    /**
     * 死信队列
     * @return
     */
    @Bean("seckillGoodsDeadQueue")
    public Queue seckillGoodsDeadQueue(){
        return QueueBuilder.durable("seckill_goods_dead_queue")
                .withArgument("x-dead-letter-exchange","seckill_goods_dead_exchange")
                .withArgument("x-dead-letter-routing-key","seckill.goods.nomal")
                .build();
    }

    /**
     * 死信队列绑定
     * @param seckillGoodsNomalExchange
     * @param seckillGoodsDeadQueue
     * @return
     */
    @Bean
    public Binding seckillGoodsDeadBanding(@Qualifier("seckillGoodsNomalExchange") Exchange seckillGoodsNomalExchange,
                                           @Qualifier("seckillGoodsDeadQueue") Queue seckillGoodsDeadQueue){
        return BindingBuilder.bind(seckillGoodsDeadQueue).to(seckillGoodsNomalExchange).with("seckill.goods.dead").noargs();
    }

    /**
     * 死信交换机
     * @return
     */
    @Bean("seckillGoodsDeadExchange")
    public Exchange seckillGoodsDeadExchange(){
        return ExchangeBuilder.directExchange("seckill_goods_dead_exchange").build();
    }

    /**
     * 正常队列
     * @return
     */
    @Bean("seckillGoodsNomalQueue")
    public Queue seckillGoodsNomalQueue(){
        return QueueBuilder.durable("seckill_goods_nomal_queue").build();
    }

    /**
     * 正常队列绑定
     * @param seckillGoodsDeadExchange
     * @param seckillGoodsNomalQueue
     * @return
     */
    @Bean
    public Binding seckillGoodsNomalBinding(@Qualifier("seckillGoodsDeadExchange") Exchange seckillGoodsDeadExchange,
                                            @Qualifier("seckillGoodsNomalQueue") Queue seckillGoodsNomalQueue){
        return BindingBuilder.bind(seckillGoodsNomalQueue).to(seckillGoodsDeadExchange).with("seckill.goods.nomal").noargs();
    }
}
