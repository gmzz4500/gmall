package com.yyds.gmall.seckill.service;

import com.yyds.gmall.model.activity.SeckillGoods;

import java.util.List;

/**
 * @ClassName: SeckillGoodsService
 * @Author: yyd
 * @Date: 2022/8/15/015
 * @Description: 秒杀商品的接口类
 */
public interface SeckillGoodsService {
    /**
     * 根据时间段查询指定的商品
     * @param time
     * @return
     */
    public List<SeckillGoods> getSeckillGoods(String time);
    
    /**
     * 查询指定的秒杀商品
     * @param time
     * @param goodsId
     * @return
     */
    public SeckillGoods getSeckillGoods(String time, String goodsId);
    
    /**
     * 同步商品的库存数据
     * @param key
     */
    public void mergeSeckillGoodsStockToDb(String key);
}
