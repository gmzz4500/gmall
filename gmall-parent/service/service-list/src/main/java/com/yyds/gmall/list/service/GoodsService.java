package com.yyds.gmall.list.service;

/**
 * @ClassName: GoodsService
 * @Author: yyd
 * @Date: 2022/8/5/005
 * @Description:es中商品相关的接口
 */
public interface GoodsService {
    /**
     * 将数据库中上架的商品写入到es
     * @param skuId
     */
    public void addGoodsToEs(Long skuId);

    /**
     * 移除商品
     * @param goodsId
     */
    public void removeGoodsFromEs(Long goodsId);

    /**
     * 为商品加热度值
     * @param goodsId
     */
    public void addHotScore(Long goodsId);
}
