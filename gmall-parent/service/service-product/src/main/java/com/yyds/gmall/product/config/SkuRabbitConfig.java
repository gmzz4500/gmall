package com.yyds.gmall.product.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: SkuRabbitConfig
 * @Author: yyd
 * @Date: 2022/8/12/012
 * @Description: 商品上下架的rabbitmq的配置
 */
@Configuration
public class SkuRabbitConfig {
    /**
     * 创建交换机
     * @return
     */
    @Bean("skuExchange")
    public Exchange skuExchange(){
        return ExchangeBuilder.directExchange("sku_exchange").build();
    }
    
    /**
     * 创建上架队列
     * @return
     */
    @Bean("skuUpperQueue")
    public Queue skuUpperQueue(){
        return QueueBuilder.durable("sku_upper_queue").build();
    }
    
    /**
     * 创建下架队列
     * @return
     */
    @Bean("skuDownQueue")
    public Queue skuDownQueue(){
        return QueueBuilder.durable("sku_down_queue").build();
    }
    
    /**
     * 上架绑定
     * @param skuExchange
     * @param skuUpperQueue
     * @return
     */
    @Bean
    public Binding upperBinding(@Qualifier("skuExchange") Exchange skuExchange,
                                @Qualifier("skuUpperQueue") Queue skuUpperQueue){
        return BindingBuilder.bind(skuUpperQueue).to(skuExchange).with("sku.upper").noargs();
    }
    
    /**
     * 下架绑定
     * @param skuExchange
     * @param skuDownQueue
     * @return
     */
    @Bean
    public Binding downBinding(@Qualifier("skuExchange") Exchange skuExchange,
                               @Qualifier("skuDownQueue") Queue skuDownQueue){
        return BindingBuilder.bind(skuDownQueue).to(skuExchange).with("sku.down").noargs();
    }
}
