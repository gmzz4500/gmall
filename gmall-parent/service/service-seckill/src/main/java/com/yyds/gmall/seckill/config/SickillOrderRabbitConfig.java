package com.yyds.gmall.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: SickillOrderRabbitConfig
 * @Author: yyd
 * @Date: 2022/8/15/015
 * @Description:秒杀下单的消息队列配置
 */
@Configuration
public class SickillOrderRabbitConfig {
    /**
     * 创建交换机
     *
     * @return
     */
    @Bean("seckillOrderExchage")
    public Exchange seckillOrderExchage() {
        return ExchangeBuilder
                .directExchange("seckill_order_exchange")
                .build();
    }
    
    /**
     * 秒杀下单的队列
     * @return
     */
    @Bean("seckillOrderQueue")
    public Queue seckillOrderQueue(){
        return QueueBuilder
                .durable("seckill_order_queue")
                .build();
    }
    
    /**
     * 创建绑定
     * @param seckillOrderExchage
     * @param seckillOrderQueue
     * @return
     */
    @Bean
    public Binding seckillOrderBinding(@Qualifier("seckillOrderExchage") Exchange seckillOrderExchage,
                                       @Qualifier("seckillOrderQueue") Queue seckillOrderQueue){
        return BindingBuilder
                .bind(seckillOrderQueue())
                .to(seckillOrderExchage())
                .with("seckill.order.add")
                .noargs();
    }
}
