package com.yyds.gmall.product.controller;

import com.yyds.gmall.common.cache.Java0217Cache;
import com.yyds.gmall.model.product.*;
import com.yyds.gmall.product.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ItemController
 * @Author: yyd
 * @Date: 2022/8/1/001
 * @Description:
 */
@RestController
@RequestMapping(value = "/api/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    /**
     * 查询sku的详细信息
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getSkuInfo/{skuId}")
    @Java0217Cache(prefix = "getSkuInfo:")
    public SkuInfo getSkuInfo(@PathVariable(value = "skuId") Long skuId) {
        return itemService.getSkuInfo(skuId);
    }

    /**
     * 查询商品详情分类的信息
     * @param category3Id
     * @return
     */
    @GetMapping(value = "/getCategory/{category3Id}")
    @Java0217Cache(prefix = "getCategory:")
    public BaseCategoryView getCategory(@PathVariable(value = "category3Id") Long category3Id){
        return itemService.getCategory(category3Id);
    }

    /**
     * 查询商品的图片列表
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getImageList/{skuId}")
    @Java0217Cache(prefix = "getImageList:")
    public List<SkuImage> getImageList(@PathVariable(value = "skuId") Long skuId){
        return itemService.getImageList(skuId);
    }

    /**
     * 查询商品的价格列表
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getPrice/{skuId}")
    @Java0217Cache(prefix = "getPrice:")
    public BigDecimal getPrice(@PathVariable(value = "skuId") Long skuId){
        return itemService.getPrice(skuId);
    }

    /**
     * 根据spuid和skuid查询商品的销售属性信息并且标识出当前sku的销售属性是哪几个
     * @param spuId
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getSpuSaleAttr/{spuId}/{skuId}")
    @Java0217Cache(prefix = "getSpuSaleAttr:")
    public List<SpuSaleAttr> getSpuSaleAttr(@PathVariable(value = "spuId") Long spuId,
                                            @PathVariable(value = "skuId") Long skuId){
        return itemService.getSpuSaleAttr(spuId,skuId);
    }

    /**
     * 根据spu的id查询键值对
     * @param spuId
     * @return
     */
    @GetMapping(value = "/getSkuIdAndValues/{spuId}")
    @Java0217Cache(prefix = "getSkuIdAndValues:")
    public Map getSkuIdAndValues(@PathVariable(value = "spuId") Long spuId){
        return itemService.getSkuIdAndValues(spuId);
    }

    /**
     * 查询指定的品牌信息
     * @param id
     * @return
     */
    @GetMapping(value = "/getBaseTrademark/{id}")
    public BaseTrademark getBaseTrademark(@PathVariable(value = "id") Long id){
        return itemService.getBaseTrademark(id);
    }

    /**
     * 查询指定sku的平台属性
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getBaseAttrInfo/{skuId}")
    public List<BaseAttrInfo> getBaseAttrInfo(@PathVariable(value = "skuId") Long skuId){
        return itemService.getBaseAttrInfo(skuId);
    }
    
    /**
     * 扣减库存
     * @param skuParam
     */
    @GetMapping(value = "/decountStocks")
    public void decountStocks(@RequestParam Map<String, Object> skuParam){
        itemService.decountStocks(skuParam);
    }
    
    /**
     * 回滚库存
     *
     * @param skuParam
     */
    @GetMapping(value = "/rollbackStocks")
    public void rollbackStocks(@RequestParam Map<String, Object> skuParam) {
        itemService.rollbackStocks(skuParam);
    }
}
