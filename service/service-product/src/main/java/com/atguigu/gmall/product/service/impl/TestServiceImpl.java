package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.service.TestService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Date:2022/8/2
 * Author:cjc
 * Description:
 */

@Service
public class TestServiceImpl implements TestService {
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 测试
     */
    @Override
    public synchronized void setRedis() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        //加锁
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid,10, TimeUnit.SECONDS);
        //true就是拿到锁进去执行
        if (lock){
            //从redis中拿去key为java0217的value
            Integer java0217 = (Integer) redisTemplate.opsForValue().get("java0217");
            //判断value是否为空
            if (java0217 != null){
                //执行加一
                java0217++;
                redisTemplate.opsForValue().set("java0217",java0217);
            }
            //继续lock的值判断
            String lockValue = (String) redisTemplate.opsForValue().get("lock");
            if (lockValue.equals(uuid)){
                //释放锁
                redisTemplate.delete("lock");
            }
        }else {
            try {
                //没拿到,睡0.1秒
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //继续递归拿锁
            setRedis();
        }
    }
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public void setRedission() {
        //加锁
        RLock lock = redissonClient.getLock("lock");
        //判断锁是否成功
        try {
            if (lock.tryLock(100,10,TimeUnit.SECONDS)) {
               //加锁成功
                try {
                    //从redis中拿去key为java0217的value
                    Integer java0217 = (Integer) redisTemplate.opsForValue().get("java0217");
                    //判断value是否为空
                    if (java0217 != null){
                        //执行加一
                        java0217++;
                        redisTemplate.opsForValue().set("java0217",java0217);
                    }
                }catch (Exception e){
                    System.out.println("加锁成功代码出现异常");
                }finally {
                    lock.unlock();
                }
            }else{
                System.out.println("枪锁失败!!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
