package com.yyds.gmall.order.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: OrderRabbitmqConfig
 * @Author: yyd
 * @Date: 2022/8/13/013
 * @Description: 订单延迟消息的配置类
 */
@Configuration
public class OrderRabbitmqDelayConfig {
    /**
     * 创建订单的正常交换机
     */
    @Bean("orderNomalExchange")
    public Exchange orderNomalExchange(){
        return ExchangeBuilder
                .directExchange("order_nomal_exchange")
                .build();
    }
    
    /**
     * 创建订单的死信队列
     */
    @Bean("orderDeadQueue")
    public Queue orderDeadQueue(){
        return QueueBuilder
                .durable("order_dead_queue")
                .withArgument("x-dead-letter-exchange","order_dead_exchange")
                .withArgument("x-dead-letter-routing-key","order.nomal")
                .build();
    }
    
    /**
     * 死信队列和正常交换机绑定
     * @param orderNomalExchange
     * @param orderDeadQueue
     * @return
     */
    @Bean
    public Binding orderDeadBonding(@Qualifier("orderNomalExchange") Exchange orderNomalExchange,
                                    @Qualifier("orderDeadQueue") Queue orderDeadQueue){
        return BindingBuilder
                .bind(orderDeadQueue)
                .to(orderNomalExchange)
                .with("order.dead").noargs();
    }
    
    /**
     * 创建订单的死信交换机
     */
    @Bean("orderDeadExchange")
    public Exchange orderDeadExchange(){
        return ExchangeBuilder
                .directExchange("order_dead_exchange")
                .build();
    }
    
    /**
     * 订单的正常队列
     */
    @Bean("orderNomalQueue")
    public Queue orderNomalQueue(){
        return QueueBuilder
                .durable("order_nomal_queue")
                .build();
    }
    
    /**
     * 死信交换机和正常队列绑定
     * @param orderDeadExchange
     * @param orderNomalQueue
     * @return
     */
    @Bean
    public Binding orderNomalBinding(@Qualifier("orderDeadExchange") Exchange orderDeadExchange,
                                     @Qualifier("orderNomalQueue") Queue orderNomalQueue){
        return BindingBuilder
                .bind(orderNomalQueue)
                .to(orderDeadExchange)
                .with("order.nomal")
                .noargs();
    }
}
