package com.atguigu.gmall.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * Date:2022/8/8
 * Author:cjc
 * Description:用户认证微服务主启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.atguigu.gmall")
@ServletComponentScan("com.atguigu.gmall.user.filter")
public class UserApplication{
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class,args);
    }
}
