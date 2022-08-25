package com.yyds.gmall.seckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @ClassName: SeckillApplication
 * @Author: yyd
 * @Date: 2022/8/15/015
 * @Description: 秒杀微服务的启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.yyds.gmall")
@EnableScheduling
public class SeckillApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeckillApplication.class,args);
    }
}
