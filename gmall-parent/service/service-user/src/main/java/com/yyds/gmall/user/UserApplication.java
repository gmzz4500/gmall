package com.yyds.gmall.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @ClassName: UserApplication
 * @Author: yyd
 * @Date: 2022/8/8/008
 * @Description: 用户微服务的启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.yyds.gmall")
@ServletComponentScan("com.yyds.gmall.user.filter")
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class,args);
    }
}
