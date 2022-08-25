package com.yyds.gmall.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @ClassName: WebApplication
 * @Author: yyd
 * @Date: 2022/8/3/003
 * @Description:
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.yyds.gmall")
@EnableFeignClients(basePackages = {"com.yyds.gmall.item.feign","com.yyds.gmall.product.feign","com.yyds.gmall.list.feign"})
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class,args);
    }
}
