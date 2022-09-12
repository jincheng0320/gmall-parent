package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.order.service.OrderInfoService;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Date:2022/8/12
 * Author:cjc
 * Description:监听订单支付信息
 */
@Component
@Log4j2
public class OrderPayListener {
    @Autowired
    private OrderInfoService orderInfoService;

    @RabbitListener(queues = "pay_queue")
    public void timeOut(Channel channel, Message message){
        //获取订单id
        byte[] body = message.getBody();
        String s = new String(body);
        //获取消息属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息编号
        long deliveryTag = messageProperties.getDeliveryTag();
        try {
            //修改订单状态
            orderInfoService.updateOrder(s);
            //确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            try {
                //是否重试过
                if (messageProperties.getRedelivered()){
                    //System.out.println("丢弃");
                    log.error("连续两次消费修改订单支付结果消息失败,支付的报文为:" + s);
                    //取消重试
                    channel.basicReject(deliveryTag, false);
                }else{
                    //重试一次
                    channel.basicReject(deliveryTag, true);
                }
            } catch (Exception ex) {
                log.error("拒绝消息失败,修改订单消息同步失败, 支付报文为:"+s);
            }
        }
    }
}
