package com.yyds.gmall.seckill.service.impl;

import com.yyds.gmall.model.activity.SeckillGoods;
import com.yyds.gmall.seckill.mapper.SeckillGoodsMapper;
import com.yyds.gmall.seckill.service.SeckillGoodsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * @ClassName: SeckillGoodsServiceImpl
 * @Author: yyd
 * @Date: 2022/8/15/015
 * @Description: 秒杀商品接口类的实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Log4j2
public class SeckillGoodsServiceImpl implements SeckillGoodsService {
    
    @Autowired
    private RedisTemplate redisTemplate;
    
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    
    /**
     * 根据时间段查询指定的商品
     *
     * @param time
     * @return
     */
    @Override
    public List<SeckillGoods> getSeckillGoods(String time) {
        
        return redisTemplate.opsForHash().values(time);
    }
    
    /**
     * 查询指定的秒杀商品
     *
     * @param time
     * @param goodsId
     * @return
     */
    @Override
    public SeckillGoods getSeckillGoods(String time, String goodsId) {
        return (SeckillGoods) redisTemplate.opsForHash().get(time, goodsId);
    }
    
    /**
     * 同步商品的库存数据
     *
     * @param key
     */
    @Override
    public void mergeSeckillGoodsStockToDb(String key) {
        //从redis中将所有商品的信息获取到
        Set seckillGoodsIds =
                redisTemplate.opsForHash().keys("SeckillGoodsStockCount_" + key);
        if (seckillGoodsIds != null && seckillGoodsIds.size() > 0) {
            //遍历同步
            seckillGoodsIds.stream().forEach(goodsId -> {
                try {
                    //获取商品的剩余库存
                    Integer stock =
                            (Integer) redisTemplate.opsForHash().get("SeckillGoodsStockCount_" + key, goodsId);
                    //同步商品数据
                    int i = seckillGoodsMapper.updateSeckillGoodsStock(Long.valueOf(goodsId.toString()), stock);
                    if (i < 0) {
                        //可选择记录到redis,mysql,日志
                        log.error("商品库存同步失败,商品的id为:" + goodsId);
                    }
                    //同步成功,需要将这个商品的剩余库存数据从redis的hash数据中移除掉,当所有的hash中的key全部被移除,整个hash会删除
                    redisTemplate.opsForHash().delete("SeckillGoodsStockCount_" + key, goodsId);
                } catch (Exception e) {
                    //可选择记录到redis,mysql ,日志中,随自己选
                    log.error("商品库存同步失败,商品的id为:" + goodsId);
                }
            });
        }
    }
}
