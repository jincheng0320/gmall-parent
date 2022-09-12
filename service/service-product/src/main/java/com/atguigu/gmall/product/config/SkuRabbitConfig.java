package com.atguigu.gmall.product.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Date:2022/8/12
 * Author:cjc
 * Description:上架或下架 交换机队列配置
 */
@Configuration
public class SkuRabbitConfig {
    /**
     * 创建交换机
     * @return
     */
    @Bean("skuExchange")
    public Exchange skuExchange(){
        return ExchangeBuilder.directExchange("sku_exchange").build();
    }

    /**
     * 创建上架队列
     * @return
     */
    @Bean("skuUpper")
    public Queue skuUpper(){
        return QueueBuilder.durable("sku_upper_queue").build();
    }

    /**
     * 创建下架队列
     * @return
     */
    @Bean("skuDown")
    public Queue skuDown(){
        return QueueBuilder.durable("sku_down_queue").build();
    }

    /**
     * 上架绑定
     * @param skuExchange
     * @param skuUpper
     * @return
     */
    @Bean
    public Binding upperBinding(@Qualifier("skuExchange") Exchange skuExchange,
                                @Qualifier("skuUpper") Queue skuUpper){
        return BindingBuilder.bind(skuUpper).to(skuExchange).with("sku.upper").noargs();
    }

    /**
     * 下架绑定
     * @param skuExchange
     * @param skuDown
     * @return
     */
    @Bean
    public Binding downBinding(@Qualifier("skuExchange") Exchange skuExchange,
                                @Qualifier("skuDown") Queue skuDown){
        return BindingBuilder.bind(skuDown).to(skuExchange).with("sku.down").noargs();
    }
}
