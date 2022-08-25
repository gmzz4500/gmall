package com.yyds.gmall.seckill.listener;

import com.rabbitmq.client.Channel;
import com.yyds.gmall.seckill.service.SeckillOrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @ClassName: SeckillOrderTimeoutListener
 * @Author: yyd
 * @Date: 2022/8/17/017
 * @Description:监听秒杀订单超时消息的消费者
 */
@Component
@Log4j2
public class SeckillOrderTimeoutListener {
    
    @Autowired
    private SeckillOrderService seckillOrderService;
    
    public void seckillOrderTimeout(Channel channel, Message message){
        //获取消息
        byte[] body = message.getBody();
        //将消息转换用户名
        String username = new String(body);
        //获取消息的属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息的编号
        long deliveryTag = messageProperties.getDeliveryTag();
        //消费消息
        try {
            //取消订单:超时取消
            seckillOrderService.cancelSeckillOrder(username);
            //确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            try {
                //每个用户一次机会
                channel.basicReject(deliveryTag, false);
            } catch (IOException ex) {
                log.error("拒绝消息失败,超时取消订单失败,订单号为:" + e.getMessage());
            }
        }
    
    }
}
