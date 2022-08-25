package com.yyds.gmall.payment;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * @ClassName: PaymentApplication
 * @Author: yyd
 * @Date: 2022/8/14/014
 * @Description: 支付微服务的启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.yyds.gmall")
public class PaymentApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class,args);
    }
    
    @Value("${ali.alipayUrl}")
    private String alipayUrl;
    
    @Value("${ali.appId}")
    private String appId;
    
    @Value("${ali.appPrivateKey}")
    private String appPrivateKey;
    
    @Value("${ali.alipayPublicKey}")
    private String alipayPublicKey;
    
    @Value("${ali.returnPaymentUrl}")
    private String returnPaymentUrl;
    
    @Value("${ali.notifyPaymentUrl}")
    private String notifyPaymentUrl;
    
    @Bean
    public AlipayClient alipayClientInitial(){
        //支付宝支付的客户端对象初始化
        return new DefaultAlipayClient(alipayUrl,
                        appId,
                        appPrivateKey,
                        "json",
                        "utf-8",
                        alipayPublicKey,
                        "RSA2");
    }
}
