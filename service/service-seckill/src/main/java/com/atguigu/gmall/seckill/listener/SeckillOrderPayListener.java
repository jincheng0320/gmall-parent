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
 * 监听秒杀订单的支付消息
 */
@Component
@Log4j2
public class SeckillOrderPayListener {

    @Autowired
    private SeckillOrderService seckillOrderService;

    /**
     * 监听秒杀的支付结果
     * @param channel
     * @param message
     */
    @RabbitListener(queues = "seckill_order_pay_queue")
    public void seckillOrderPay(Channel channel, Message message){
        //获取消息
        byte[] body = message.getBody();
        //将消息转换为json字符串
        String s = new String(body);
        //获取消息的属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息的编号
        long deliveryTag = messageProperties.getDeliveryTag();
        //消费消息
        try {
            //秒杀订单支付结果修改
            seckillOrderService.updateSeckillOrder(s);
            //确认消息
            channel.basicAck(deliveryTag, false);
        }catch (Exception e){
            try {
                if(messageProperties.getRedelivered()){
                    log.error("拒绝消息失败,修改秒杀订单的支付结果失败, 支付的报文为:" + s);
                    channel.basicReject(deliveryTag, false);
                }else{
                    //一次失败再来一次
                    channel.basicReject(deliveryTag, true);
                }
            }catch (Exception e1){
                log.error("拒绝消息失败,修改秒杀订单的支付结果失败, 支付的报文为:" + s);
            }
        }
    }
}
