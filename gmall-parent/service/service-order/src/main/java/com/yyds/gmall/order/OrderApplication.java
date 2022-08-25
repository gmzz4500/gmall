package com.yyds.gmall.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @ClassName: OrderApplication
 * @Author: yyd
 * @Date: 2022/8/10/010
 * @Description: 订单微服务的启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.yyds.gmall")
@EnableFeignClients(basePackages = {"com.yyds.gmall.cart.feign","com.yyds.gmall.product.feign"})
@ServletComponentScan("com.yyds.gmall.order.filter")
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class,args);
    }
}
