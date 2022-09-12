package com.atguigu.gmall.seckill.listener;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.seckill.pojo.UserRecode;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
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
 * Description:监听商品数据同步
 */
@Component
@Log4j2
public class SeckillGoodsListener {
    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @RabbitListener(queues = "seckill_goods_nomal_queue")
    public void seckillGoodsListener(Channel channel, Message message){
        //获取信息
        byte[] body = message.getBody();
        //转换json字符串
        String key = new String(body);
        //获取消息属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息编号
        long deliveryTag = messageProperties.getDeliveryTag();
        try {
            //秒杀下单
            seckillGoodsService.mergeSeckillGoodsStockToDb(key);
            //确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            try{
                //每个用户一次机会
                channel.basicReject(deliveryTag, false);
            } catch (Exception e1){
                log.error("拒绝消息失败,秒杀商品数据同步失败,失败的商品时间段为:"+key);
            }
        }
    }
}
