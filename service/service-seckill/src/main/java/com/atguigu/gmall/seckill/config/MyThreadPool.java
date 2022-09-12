package com.atguigu.gmall.seckill.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Date:2022/8/3
 * Author:cjc
 * Description:自定义线程池
 */
@Configuration
public class MyThreadPool {
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        return new ThreadPoolExecutor(6,
                12,
                10,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10000));
    }
}
