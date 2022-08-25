package com.yyds.gmall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @ClassName: ProductApplication
 * @Author: yyd
 * @Date: 2022/7/26/026
 * @Description:
 */

/**
 * 商品管理微服务的启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.yyds.gmall")
@EnableFeignClients(basePackages = "com.yyds.gmall.list.feign")
public class ProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class,args);
    }
}
