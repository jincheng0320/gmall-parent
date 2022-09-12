package com.atguigu.gmall.common.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import springfox.documentation.spring.web.json.Json;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 自定义切面类
 */
@Component
@Aspect
public class GmallCacheAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 增强方法
     * @param point:切入点
     * @return
     */
    @Around("@annotation(com.atguigu.gmall.common.cache.Java0217Cache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint point){
        //返回结果初始化
        Object result = null;
        try {
            //获取方法参数
            Object[] args = point.getArgs();
            //获取方法签名:整个方法对象
            MethodSignature signature = (MethodSignature) point.getSignature();
            //获取这个方法的指定类型的注解
            Java0217Cache java0217Cache = signature.getMethod().getAnnotation(Java0217Cache.class);
            //获取前缀属性
            String prefix = java0217Cache.prefix();
            // 从缓存中获取数据 key = 前缀+参数数组
            String key = prefix+Arrays.asList(args).toString();

            // 从redis中获取指定数据
            result = cacheHit(signature, key);
            //判断redis是否有数据
            if (result!=null){
                // 缓存有数据
                return result;
            }
            // redis没有数据 获取锁
            RLock lock = redissonClient.getLock(key+"lock");
            //尝试加锁
            if (lock.tryLock(100, 100, TimeUnit.SECONDS)){
               try {
                   try {
                       //执行方法
                       result = point.proceed(point.getArgs());
                       // 防止缓存穿透
                       if (null==result){
                           // 数据库没有数据
                           Object o = new Object();
                           this.redisTemplate.opsForValue().set(key, JSONObject.toJSONString(o),300,TimeUnit.SECONDS);
                           return o;
                       }else{
                            //数据库有数据
                           this.redisTemplate.opsForValue().set(key, JSONObject.toJSONString(result), 24*3600,TimeUnit.SECONDS);
                       }
                   } catch (Throwable throwable) {
                       throwable.printStackTrace();
                   }
                   return result;
               }catch (Exception e){
                   e.printStackTrace();
               }finally {
                   // 释放锁
                   lock.unlock();
               }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //boolean flag = lock.tryLock(10L, 10L, TimeUnit.SECONDS);
        return result;
    }

    /**
     * 从redis缓存中获取数据
     * @param signature
     * @param key
     * @return
     */
    private Object cacheHit(MethodSignature signature, String key) {
        //使用参数的key获取redis缓存数据 ,获取到的数据是一个json类型数据
        String cache = (String)redisTemplate.opsForValue().get(key);
        //redis中有数据,isNotBlank:不为空白
        if (StringUtils.isNotBlank(cache)) {
            // 有，则反序列化，直接返回
            Class returnType = signature.getReturnType(); // 获取方法返回类型
            // 不能使用parseArray<cache, T>，因为不知道List<T>中的泛型
            return JSONObject.parseObject(cache, returnType);
        }
        //redis没有数据返回空i
        return null;
    }

}
