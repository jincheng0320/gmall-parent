package com.atguigu.gmall.order.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Date:2022/8/12
 * Author:cjc
 * Description:订单交换机和队列配置
 */
@Configuration
public class OrderRabbitConfig {
    /**
     * 正常交换机
     * @return
     */
    @Bean("orderNormalExchange")
    public Exchange orderNormalExchange(){
        return ExchangeBuilder.directExchange("order_normal_exchange").build();
    }

    /**
     * 死信队列
     * @return
     */
    @Bean("orderDeadQueue")
    public Queue orderDeadQueue(){
        return QueueBuilder
                .durable("order_dead_queue")
                .withArgument("x-dead-letter-exchange","order_dead_exchange")
                .withArgument("x-dead-letter-routing-key","order.normal")
                .build();
    }

    /**
     * 正常交换机和死信队列绑定
     * @param orderNormalExchange
     * @param orderDeadQueue
     * @return
     */
    @Bean
    public Binding deadBinding(@Qualifier("orderNormalExchange") Exchange orderNormalExchange,
                               @Qualifier("orderDeadQueue") Queue orderDeadQueue){
        return BindingBuilder.bind(orderDeadQueue).to(orderNormalExchange).with("order.dead").noargs();
    }

    /**
     * 死信交换机
     * @return
     */
    @Bean("orderDeadExchange")
    public Exchange orderDeadExchange(){
        return ExchangeBuilder.directExchange("order_dead_exchange").build();
    }

    /**
     * 正常队列
     * @return
     */
    @Bean("orderNormalQueue")
    public Queue orderNormalQueue(){
        return QueueBuilder.durable("order_normal_queue").build();
    }

    /**
     * 死信交换机和正常队列绑定
     * @param orderDeadExchange
     * @param orderNormalQueue
     * @return
     */
    @Bean
    public Binding normalBinding(@Qualifier("orderDeadExchange") Exchange orderDeadExchange,
                                 @Qualifier("orderNormalQueue") Queue orderNormalQueue){
        return BindingBuilder.bind(orderNormalQueue).to(orderDeadExchange).with("order.normal").noargs();
    }
}
