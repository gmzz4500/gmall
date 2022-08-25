package com.yyds.gmall.seckill.listener;

import com.rabbitmq.client.Channel;
import com.yyds.gmall.seckill.service.SeckillGoodsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName: SeckillGoodsFromRedisToDbListener
 * @Author: yyd
 * @Date: 2022/8/16/016
 * @Description:监听秒杀商品活动结束同步库存数据的消费者
 */
@Component
@Log4j2
public class SeckillGoodsFromRedisToDbListener {
    @Autowired
    private SeckillGoodsService seckillGoodsService;
    /**
     * 同步库存数据
     * @param channel
     * @param message
     */
    @RabbitListener(queues = "seckill_goods_normal_queue")
    public void seckillGoodsFromRedisToDb(Channel channel, Message message){
        //获取消息
        byte[] body = message.getBody();
        //获取需要同步的商品所属的时间段
        String key = new String(body);
        //获取消息的属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息的编号
        long deliveryTag = messageProperties.getDeliveryTag();
        //消费消息
        try {
            //秒杀商品数据同步--TODO
            seckillGoodsService.mergeSeckillGoodsStockToDb(key);
            //确认消息
            channel.basicAck(deliveryTag, false);
        }catch (Exception e){
            try {
                //给每个用户一次机会
                channel.basicReject(deliveryTag, false);
            }catch (Exception e1){
                log.error("拒绝消息失败,秒杀商品的数据同步失败,商品所属的时间段为:" + key);
            }
        }
    }
}
