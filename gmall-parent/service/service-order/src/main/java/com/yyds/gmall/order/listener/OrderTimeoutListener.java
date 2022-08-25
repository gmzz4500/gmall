package com.yyds.gmall.order.listener;

import com.rabbitmq.client.Channel;
import com.yyds.gmall.order.service.OrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @ClassName: OrderTimeoutListener
 * @Author: yyd
 * @Date: 2022/8/13/013
 * @Description: 监听订单超时消息:无论订单最后是否被支付了或者取消了,都会收到消息
 */
@Component
@Log4j2
public class OrderTimeoutListener {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 监听每笔订单的延迟消息:只有前置状态为:未支付的订单才进行---超时取消
     *
     * @param channel
     * @param message
     */
    @RabbitListener(queues = "order_nomal_queue")
    public void cancelTimeoutOrder(Channel channel, Message message) {
        //获取消息
        byte[] body = message.getBody();
        //将消息转换为订单id
        long orderId = Long.parseLong(new String(body));
        //获取消息的属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息的编号
        long deliveryTag = messageProperties.getDeliveryTag();
        //消费消息
        try {
            //超时取消订单
            orderService.cancelOrder(orderId);
            //确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            try {
                //确认或消费失败,再来一次
                if (messageProperties.getRedelivered()) {
                    log.error("连续两次消费超时取消订单消息失败,订单的id为:" + orderId);
                    //这是第二次了
                    channel.basicReject(deliveryTag, false);
                } else {
                    //第一次,再来一次
                    channel.basicReject(deliveryTag, true);
                }
            } catch (Exception ex) {
                log.error("拒绝消息失败,超时取消订单消息同步失败,订单的id为:" + orderId);
            }
        }
    }
}
