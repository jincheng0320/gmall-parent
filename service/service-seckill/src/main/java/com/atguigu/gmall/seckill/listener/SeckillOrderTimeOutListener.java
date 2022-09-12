package com.atguigu.gmall.seckill.listener;

import com.atguigu.gmall.seckill.service.SeckillOrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Date:2022/8/16
 * Author:cjc
 * Description:监听秒杀订单超时消息
 */
@Component
@Log4j2
public class SeckillOrderTimeOutListener {

    @Autowired
    private SeckillOrderService seckillOrderService;

    @RabbitListener(queues = "seckill_order_nomal_queue")
    public void seckillGoodsListener(Channel channel, Message message){
        //获取信息
        byte[] body = message.getBody();
        //转换json字符串
        String username = new String(body);
        //获取消息属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息编号
        long deliveryTag = messageProperties.getDeliveryTag();
        try {
            //超时取消订单
            seckillOrderService.cancelSeckillOrder(username);
            //确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            try{
                //每个用户一次机会
                channel.basicReject(deliveryTag, false);
            } catch (Exception e1){
                log.error("拒绝消息失败,秒杀商品数据同步失败,失败的商品时间段为:"+e.getMessage());
            }
        }
    }
}
