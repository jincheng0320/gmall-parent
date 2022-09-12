package com.atguigu.gmall.payment.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Date:2022/8/13
 * Author:cjc
 * Description:支付结果发送mq的队列配置
 */
@Configuration
public class PayRabbitConfig {

    /**
     * 支付交换机
     * @return
     */
    @Bean("payExchange")
    public Exchange payExchange(){
        return ExchangeBuilder.directExchange("pay_exchange").build();
    }

    /**
     * 支付结果队列
     * @return
     */
    @Bean("payQueue")
    public Queue payQueue(){
        return QueueBuilder.durable("pay_queue").build();
    }

    /**
     * 秒杀订单的支付结果通知队列
     * @return
     */
    @Bean("seckillOrderPayQueue")
    public Queue seckillOrderPayQueue(){
        return QueueBuilder.durable("seckill_order_pay_queue").build();
    }

    /**
     * 普通支付交换机和支付队列绑定
     * @param payExchange
     * @param payQueue
     * @return
     */
    @Bean
    public Binding payBinding(@Qualifier("payExchange") Exchange payExchange,
                              @Qualifier("payQueue")  Queue payQueue){
        return BindingBuilder.bind(payQueue).to(payExchange).with("pay.order").noargs();
    }

    /**
     * 支付交换机和秒杀支付队列绑定
     * @param payExchange
     * @param seckillOrderPayQueue
     * @return
     */
    @Bean
    public Binding seckillOrderPayBinding(@Qualifier("payExchange") Exchange payExchange,
                                          @Qualifier("seckillOrderPayQueue") Queue seckillOrderPayQueue){
        return BindingBuilder.bind(seckillOrderPayQueue).to(payExchange).with("pay.seckill.order").noargs();
    }
}
