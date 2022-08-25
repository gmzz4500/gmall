package com.yyds.gmall.seckill.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yyds.gmall.model.activity.SeckillGoods;
import com.yyds.gmall.seckill.mapper.SeckillGoodsMapper;
import com.yyds.gmall.seckill.util.DateUtil;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: SeckillGoodsFromDbToRedisTask
 * @Author: yyd
 * @Date: 2022/8/15/015
 * @Description: 秒杀商品的数据从数据库写入redis的定时任务
 */
@Component
public class SeckillGoodsFromDbToRedisTask {
    
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    
    @Autowired
    private RedisTemplate redisTemplate;
    
    /**
     * 将秒伤商品写入redis
     * cron表达式: 秒 分 时 日 月 周 年
     * *: 任意时间
     * ?: 忽略这个段位
     * 逗号: 指定时间执行
     * 横杠: 区间
     * 间隔: 指的是每个多久执行
     * fixedRate(毫秒): 每5秒执行一次,不受方法的执行时间影响,
     * 上一次执行开始多久后执行下一次,可能导致任务并行执行
     * fixedDelay(毫秒): ,每5秒执行一次,受方法的执行时间影响,
     * 上一次执行结束多久后执行下一次,任务都是串行化执行
     * initialDelay(毫秒): 只影响第一次什么时候执行
     */
    @Scheduled(cron = "1/20 * * * * *")
    public void seckillGoodsFromDbToRedis() {
        //计算当前系统所在的时间段以及后面的4个时间段
        List<Date> dateMenus = DateUtil.getDateMenus();
        //遍历5个时间段,查询这5个时间段的商品的数据
        dateMenus.stream().forEach(start -> {
            //获取活动开始时间
            String startTime =
                    DateUtil.data2str(start, DateUtil.PATTERN_YYYY_MM_DDHHMM);
            //计算结束时间
            Date end = DateUtil.addDateHour(start, 2);
            String endTime =
                    DateUtil.data2str(end, DateUtil.PATTERN_YYYY_MM_DDHHMM);
            //计算商品数据在redis中的存活时间(毫秒值)
            long liveTime = end.getTime() - System.currentTimeMillis();
            //获取当前这个时间段存储的hash类型的key
            String key = DateUtil.data2str(start, DateUtil.PATTERN_YYYYMMDDHH);
            //拼接查询条件
            LambdaQueryWrapper<SeckillGoods> wrapper = new LambdaQueryWrapper<>();
            //商品必须审核通过
            wrapper.eq(SeckillGoods::getStatus, "1");
            //商品的活动开始时间要大于等于startTime
            wrapper.ge(SeckillGoods::getStartTime, startTime);
            //商品的结束时间小于等于endTime
            wrapper.le(SeckillGoods::getEndTime, endTime);
            //库存大于0
            wrapper.gt(SeckillGoods::getStockCount, 0);
            //redis中没有的商品
            Set keys = redisTemplate.opsForHash().keys(key);
            if (keys != null && keys.size() > 0) {
                wrapper.notIn(SeckillGoods::getId, keys);
            }
            //查询商品列表
            List<SeckillGoods>
                    seckillGoodsList = seckillGoodsMapper.selectList(wrapper);
            //遍历将商品写入到redis里面去
            seckillGoodsList.stream().forEach(seckillGoods -> {
                //将商品从数据库写入redis
                redisTemplate.opsForHash().put(key, seckillGoods.getId() + "", seckillGoods);
                //构建一个库存长度的数组
                String[] ids = getIds(seckillGoods.getStockCount(), seckillGoods.getId() + "");
                //构建一个商品库存个数长度的list队列,作为下单的依据
                redisTemplate.opsForList().leftPushAll("Seckill_Goods_Stock_Queue_" + seckillGoods.getId(), ids);
                redisTemplate.expire("Seckill_Goods_Stock_Queue_" + seckillGoods.getId(), liveTime, TimeUnit.MILLISECONDS);
                //构建一个商品库存的自增值,统计商品的剩余库存
                redisTemplate.opsForHash().increment("SeckillGoodsStockCount_" + key, seckillGoods.getId() + "", seckillGoods.getStockCount());
            });
            //设置商品数据的过期时间
            setSeckillGoodsExpire(liveTime, key);
        });
    }
    @Autowired
    private RabbitTemplate rabbitTemplate;
    /**
     * 商品活动过期,所有和这个商品相关的数据全部需要消失
     * 设置商品数据的过期时间:商品数据+库存自增值
     *
     * @param liveTime
     * @param key
     */
    private void setSeckillGoodsExpire(long liveTime, String key) {
        //确认每个时间段设置过期时间只设置一次
        Long count = redisTemplate.opsForHash().increment("SeckillGoodsExpireTimes", key, 1);
        if (count > 1) {
            return;
        }
        //设置商品数据过期
        redisTemplate.expire(key, liveTime, TimeUnit.MILLISECONDS);
        //发送延迟消息,触发数据同步
        rabbitTemplate.convertAndSend("seckill_goods_normal_exchange",
                "seckill.goods.dead",
                key,
                (message -> {
                    //获取消息的属性
                    MessageProperties messageProperties = message.getMessageProperties();
                    //设置过期时间:商品活动结束半小时以后开始同步商品的剩余库存到数据库中去
                    messageProperties.setExpiration((liveTime + 1800000) + "");
//                    messageProperties.setExpiration(300000 + "");
                    //返回
                    return message;
                }));
    }
    
    /**
     * 构建秒杀商品库存长度的数组
     *
     * @param stockCount
     * @param goodsId
     * @return
     */
    private String[] getIds(Integer stockCount, String goodsId) {
        //数组初始化
        String[] ids = new String[stockCount];
        //为每个元素赋值
        for (int i = 0; i < stockCount; i++) {
            ids[i] = goodsId;
        }
        //返回
        return ids;
    }
}
