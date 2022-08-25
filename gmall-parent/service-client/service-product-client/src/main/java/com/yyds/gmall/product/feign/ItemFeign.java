package com.yyds.gmall.product.feign;

/**
 * @ClassName: ItemFeign
 * @Author: yyd
 * @Date: 2022/8/1/001
 * @Description:
 */

import com.yyds.gmall.model.product.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/***
 * 商品详情使用相关的feign接口定义模块
 */
@FeignClient(name="service-product",path = "/api/item",contextId = "ItemFeign")
public interface ItemFeign {

    /**
     * 查询sku的详细信息
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable(value = "skuId") Long skuId);

    /**
     * 查询商品详情分类的信息
     * @param category3Id
     * @return
     */
    @GetMapping(value = "/getCategory/{category3Id}")
    public BaseCategoryView getCategory(@PathVariable(value = "category3Id") Long category3Id);

    /**
     * 查询商品的图片列表
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getImageList/{skuId}")
    public List<SkuImage> getImageList(@PathVariable(value = "skuId") Long skuId);

    /**
     * 查询商品的价格俩表
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getPrice/{skuId}")
    public BigDecimal getPrice(@PathVariable(value = "skuId") Long skuId);

    /**
     * 根据spuid和skuid查询商品的销售属性信息并且标识出当前sku的销售属性是哪几个
     * @param spuId
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getSpuSaleAttr/{spuId}/{skuId}")
    public List<SpuSaleAttr> getSpuSaleAttr(@PathVariable(value = "spuId") Long spuId,
                                            @PathVariable(value = "skuId") Long skuId);

    /**
     * 根据spu的id查询键值对
     * @param spuId
     * @return
     */
    @GetMapping(value = "/getSkuIdAndValues/{spuId}")
    public Map getSkuIdAndValues(@PathVariable(value = "spuId") Long spuId);

    /**
     * 查询指定的品牌信息
     * @param id
     * @return
     */
    @GetMapping(value = "/getBaseTrademark/{id}")
    public BaseTrademark getBaseTrademark(@PathVariable(value = "id") Long id);

    /**
     * 查询指定sku的平台属性
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getBaseAttrInfo/{skuId}")
    public List<BaseAttrInfo> getBaseAttrInfo(@PathVariable(value = "skuId") Long skuId);
    
    /**
     * 扣减库存
     * @param skuParam
     */
    @GetMapping(value = "/decountStocks")
    public void decountStocks(@RequestParam Map<String, Object> skuParam);
    
    /**
     * 回滚库存
     * @param skuParam
     */
    @GetMapping(value = "/rollbackStocks")
    public void rollbackStocks(@RequestParam Map<String, Object> skuParam);
}
