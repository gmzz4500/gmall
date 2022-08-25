package com.yyds.gmall.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @ClassName: CartApplication
 * @Author: yyd
 * @Date: 2022/8/9/009
 * @Description: 购物车微服务的启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(value = "com.yyds.gmall")
@EnableFeignClients(value = "com.yyds.gmall.product.feign")
@ServletComponentScan("com.yyds.gmall.cart.filter")
public class CartApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class,args);
    }
}
