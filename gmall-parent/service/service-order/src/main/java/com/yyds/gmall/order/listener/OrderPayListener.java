package com.yyds.gmall.order.listener;

import com.rabbitmq.client.Channel;
import com.yyds.gmall.order.service.OrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @ClassName: OrderPayListener
 * @Author: yyd
 * @Date: 2022/8/14/014
 * @Description: 监听订单支付结果消息的消费者
 */
@Component
@Log4j2
public class OrderPayListener {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 监听支付消息修改订单
     * @param channel
     * @param message
     */
    @RabbitListener(queues = "order_pay_queue")
    public void orderPay(Channel channel, Message message){
        //获取消息
        byte[] body = message.getBody();
        //将消息转换为订单id
        String s = new String(body);
        //获取消息的属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息的编号
        long deliveryTag = messageProperties.getDeliveryTag();
        //消费信息
        try {
            //修改订单的支付状态
            orderService.updateOrder(s);
            //确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            try {
                //确认或消费失败,再来一次
                if (messageProperties.getRedelivered()){
                    log.error("连续两次消费修改订单支付结果消息失败,支付的报文为:" + s);
                    //这是第二次了
                    channel.basicReject(deliveryTag, false);
                }else {
                    //第一次,再来一次
                    channel.basicReject(deliveryTag, true);
                }
            } catch (IOException ex) {
                log.error("拒绝消息失败,修改订单支付结果消息同步失败,支付的报文为:" + s);
            }
        }
    
    }
}
