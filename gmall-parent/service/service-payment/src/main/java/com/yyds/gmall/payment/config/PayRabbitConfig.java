package com.yyds.gmall.payment.config;

/**
 * @ClassName: PayRabbitConfig
 * @Author: yyd
 * @Date: 2022/8/14/014
 * @Description:
 */

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 支付结果通知的消息队列配置
 */
@Configuration
public class PayRabbitConfig {
    
    /**
     * 创建支付结果通知的交换机
     * @return
     */
    @Bean("payExchange")
    public Exchange payExchange(){
        return ExchangeBuilder.directExchange("pay_exchange").build();
    }
    
    /**
     * 创建普通订单的支付结果通知的队列
     * @return
     */
    @Bean("orderPayQueue")
    public Queue orderPayQueue(){
        return QueueBuilder.durable("order_pay_queue").build();
    }
    
    /**
     * 创建秒杀订单的支付结果通知的队列
     * @return
     */
    @Bean("seckillOrderPayQueue")
    public Queue seckillOrderPayQueue(){
        return QueueBuilder.durable("seckill_order_pay_queue").build();
    }
    
    /**
     * 支付交换机和普通订单支付结果队列绑定
     * @param payExchange
     * @param orderPayQueue
     * @return
     */
    @Bean
    public Binding orderPayBinding(@Qualifier("payExchange") Exchange payExchange,
                                   @Qualifier("orderPayQueue") Queue orderPayQueue){
        return BindingBuilder.bind(orderPayQueue).to(payExchange).with("pay.order").noargs();
    }
    
    /**
     * 支付交换机和秒杀订单支付结果队列绑定
     * @param payExchange
     * @param seckillOrderPayQueue
     * @return
     */
    @Bean
    public Binding seckillOrderPayBinding(@Qualifier("payExchange") Exchange payExchange,
                                   @Qualifier("seckillOrderPayQueue") Queue seckillOrderPayQueue){
        return BindingBuilder.bind(seckillOrderPayQueue).to(payExchange).with("pay.seckill.order").noargs();
    }
}

