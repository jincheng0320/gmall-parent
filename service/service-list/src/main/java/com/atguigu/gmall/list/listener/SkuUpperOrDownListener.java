package com.atguigu.gmall.list.listener;

import com.atguigu.gmall.list.service.GoodsService;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * Date:2022/8/12
 * Author:cjc
 * Description:上架或下架消费者
 */
@Component
@Log4j2
public class SkuUpperOrDownListener {
    @Autowired
    private GoodsService goodsService;

    /**
     * 监听上架商品到ES
     * @param channel
     * @param message
     */
    @RabbitListener(queues = "sku_upper_queue")
    public void upperConsumer(Channel channel, Message message){
        //将消息转换skuId
        String skuId = new String(message.getBody());
        //获取消息属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息编号
        long deliveryTag = messageProperties.getDeliveryTag();
        try {
           // System.out.println("商品上架啦");
            //上架同步
            goodsService.addGoodsToEs(Long.parseLong(skuId));
            //确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            try {
                //消息失败,进行重试一次
                if (messageProperties.getRedelivered()){
                    //重入二次失败 直接丢弃
                    log.error("连续两次消费失败,商品id为 : "+skuId);
                    channel.basicReject(deliveryTag, false);
                }else{
                    //第一次重试
                    channel.basicReject(deliveryTag, true);
                }
            } catch (Exception e1) {
                log.error("商品上架消息同步失败,商品id为 : "+skuId);
            }
        }
    }

    /**
     * 监听商品下架
     * @param channel
     * @param message
     */
    @RabbitListener(queues = "sku_down_queue")
    public void downConsumer(Channel channel, Message message){
        //将消息转换skuId
        String skuId = new String(message.getBody());
        //获取消息属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息编号
        long deliveryTag = messageProperties.getDeliveryTag();
        try {
            //System.out.println("商品下架了");
            //下架同步
            goodsService.removeFromEs(Long.parseLong(skuId));
            //确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            try {
                //消息失败,进行重试一次
                if (messageProperties.getRedelivered()){
                    //重入二次失败 直接丢弃
                    log.error("连续两次消费失败,商品id为 : "+skuId);
                    channel.basicReject(deliveryTag, false);
                }else{
                    //第一次重试
                    channel.basicReject(deliveryTag, true);
                }
            } catch (Exception e1) {
                log.error("商品下架消息同步失败,商品id为 : "+skuId);
            }
        }
    }
}
