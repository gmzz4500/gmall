package com.yyds.gmall.list.listener;

import com.rabbitmq.client.Channel;
import com.yyds.gmall.list.service.GoodsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName: SkuUpperOrDownListener
 * @Author: yyd
 * @Date: 2022/8/12/012
 * @Description: 商品上下架消息的消费者
 */
@Component
@Log4j2
public class SkuUpperOrDownListener {
    
    @Autowired
    private GoodsService goodsService;
    /**
     * 监听上架消息
     * @param channel
     * @param message
     */
    @RabbitListener(queues = "sku_upper_queue")
    public void sKuUpper(Channel channel, Message message){
        //获取消息
        byte[] body = message.getBody();
        //将消息转换为商品id
        String skuId = new String(body);
        //获取消息的属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息的编号
        long deliveryTag = messageProperties.getDeliveryTag();
        //消费消息
        try {
            System.out.println("上架消息消费成功,内容为:" + skuId);
            //上架同步
            goodsService.addGoodsToEs(Long.parseLong(skuId));
            //确认消息
            channel.basicAck(deliveryTag, false);
        }catch (Exception e){
            try {
                //确认或消费失败,再来一次
                if (messageProperties.getRedelivered()){
                    log.error("连续两次消费上架消息失败,商品的id为:" +skuId);
                    //这是第二次
                    channel.basicReject(deliveryTag, false);
                }else {
                    //第一次,再来一次
                    channel.basicReject(deliveryTag, true);
                }
            }catch (Exception e1){
                log.error("拒绝消息失败,商品上架消息同步失败,商品的id为:" + skuId);
            }
            
        }
    }
    
    /**
     * 监听下架消息
     * @param channel
     * @param message
     */
    @RabbitListener(queues = "sku_down_queue")
    public void sKuDown(Channel channel, Message message){
        //获取消息
        byte[] body = message.getBody();
        //将消息转换为商品id
        String skuId = new String(body);
        //获取消息的属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息的编号
        long deliveryTag = messageProperties.getDeliveryTag();
        //消费消息
        try {
            System.out.println("下架消息消费成功,内容为:" + skuId);
            //下架同步
            goodsService.removeGoodsFromEs(Long.parseLong(skuId));
            //确认消息
            channel.basicAck(deliveryTag, false);
        }catch (Exception e){
            try {
                //确认或消费失败,再来一次
                if (messageProperties.getRedelivered()){
                    log.error("连续两次消费下架消息失败,商品的id为:" +skuId);
                    //这是第二次
                    channel.basicReject(deliveryTag, false);
                }else {
                    //第一次,再来一次
                    channel.basicReject(deliveryTag, true);
                }
            }catch (Exception e1){
                log.error("拒绝消息失败,商品下架消息同步失败,商品的id为:" + skuId);
            }
            
        }
    }
}
