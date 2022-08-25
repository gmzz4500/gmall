package com.yyds.gmall.product.service;

/**
 * @ClassName: ItemService
 * @Author: yyd
 * @Date: 2022/8/1/001
 * @Description:
 */

import com.yyds.gmall.model.product.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/***
 * 供内部服务调用的接口
 */
public interface ItemService {
    /**
     * 查询商品的详细信息
     * @param skuId
     * @return
     */
    public SkuInfo getSkuInfo(Long skuId);

    /**
     * 从redis或数据库查询商品的信息
     * @param skuId
     * @return
     */
    public SkuInfo getSkuInfoFromDbOrRedis(Long skuId);

    /**
     * 根据category3Id查询一级二级三级分类的信息
     *
     * @param category3Id
     * @return
     */
    public BaseCategoryView getCategory(Long category3Id);

    /**
     * 查询图片列表
     * @param skuId
     * @return
     */
    public List<SkuImage> getImageList(Long skuId);

    /**
     * 查询价格列表
     * @param skuId
     * @return
     */
    public BigDecimal getPrice(Long skuId);

    /**
     * 根据spuid和skuid查询商品的销售属性信息并且标识出当前sku的销售属性是哪几个
     * @param spuId
     * @param skuId
     * @return
     */
    public List<SpuSaleAttr> getSpuSaleAttr(Long spuId,Long skuId);

    /**
     * 根据spu的id查询这个spu下所有sku的id和拥有销售属性值的键值对
     * @param spuId
     * @return
     */
    public Map getSkuIdAndValues(Long spuId);

    /**
     * 查询品牌的信息
     * @param id
     * @return
     */
    public BaseTrademark getBaseTrademark(Long id);

    /**
     * 查询指定sku的平台属性
     * @param skuId
     * @return
     */
    public List<BaseAttrInfo> getBaseAttrInfo(Long skuId);
    
    /**
     * 扣减库存的方法
     * @param skuParam
     */
    public void decountStocks(Map<String,Object> skuParam);
    
    /**
     * 回滚库存的方法
     *
     * @param skuParam
     */
    public void rollbackStocks(Map<String, Object> skuParam);
}
