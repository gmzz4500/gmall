package com.yyds.gmall.list;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @ClassName: ListApplicaion
 * @Author: yyd
 * @Date: 2022/8/5/005
 * @Description:搜索微服务的启动类
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@ComponentScan(value = "com.yyds.gmall")
@EnableElasticsearchRepositories(basePackages = "com.yyds.gmall.list.dao")
@EnableFeignClients(basePackages = "com.yyds.gmall.product.feign")
public class ListApplicaion {
    public static void main(String[] args) {
        SpringApplication.run(ListApplicaion.class,args);
    }
}
