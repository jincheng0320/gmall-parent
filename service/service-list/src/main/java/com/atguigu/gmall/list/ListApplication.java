package com.atguigu.gmall.list;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Date:2022/8/5
 * Author:cjc
 * Description:搜索微服务主启动类
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@ComponentScan("com.atguigu.gmall")
@EnableElasticsearchRepositories(basePackages = "com.atguigu.gmall.list.dao")
@EnableFeignClients("com.atguigu.gmall.product.feign")
public class ListApplication {
    public static void main(String[] args) {
        SpringApplication.run(ListApplication.class,args);
    }
}
