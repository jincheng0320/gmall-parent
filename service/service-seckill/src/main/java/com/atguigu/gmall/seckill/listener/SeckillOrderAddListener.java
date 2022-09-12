package com.atguigu.gmall.seckill.listener;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.seckill.pojo.UserRecode;
import com.atguigu.gmall.seckill.service.SeckillOrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;



/**
 * Date:2022/8/15
 * Author:cjc
 * Description:监听秒杀队列消息 ,异步完成下单
 */
@Component
@Log4j2
public class SeckillOrderAddListener {

    @Autowired
    private SeckillOrderService seckillOrderService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     *秒杀异步下单
     * @param channel
     * @param message
     */
    @RabbitListener(queues = "seckill_order_queue")
    public void addSeckillOrder(Channel channel, Message message){
        //获取信息
        byte[] body = message.getBody();
        //转换json字符串
        String s = new String(body);
        //反序列化为用户排队状态
        UserRecode userRecode = JSONObject.parseObject(s, UserRecode.class);
        //获取消息属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息编号
        long deliveryTag = messageProperties.getDeliveryTag();
        try {
            //秒杀下单
            seckillOrderService.realAddSeckillOrder(userRecode);
            //确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            try{
                //秒杀下单失败
                //商品不存在,秒杀下单失败
                userRecode.setStatus(3);
                userRecode.setMsg("商品不存在,秒杀失败!");
                //更新redis中排队的状态
                redisTemplate.opsForValue().set("User_Recode_" + userRecode.getUsername(), userRecode);
                //删除排队的计数器,不能影响用户购买其他的东西
                redisTemplate.delete("User_Queue_Count_" + userRecode.getUsername());
                //每个用户一次机会
                channel.basicReject(deliveryTag, false);
            } catch (Exception e1){
                log.error("拒绝消息失败,秒杀下单失败,消息为:"+s);
            }
        }
    }
}
