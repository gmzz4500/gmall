package com.yyds.gmall.item.service.impl;

import com.yyds.gmall.item.service.ItemService;
import com.yyds.gmall.model.product.BaseCategoryView;
import com.yyds.gmall.model.product.SkuImage;
import com.yyds.gmall.model.product.SkuInfo;
import com.yyds.gmall.model.product.SpuSaleAttr;
import com.yyds.gmall.product.feign.ItemFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName: ItemServiceImpl
 * @Author: yyd
 * @Date: 2022/8/1/001
 * @Description:
 */

/***
 * 商品详情页使用的接口的实现类
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemFeign itemFeign;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    /**
     * 获取商品详情页所需要的全部数据
     *
     * @param skuId
     * @return
     */
    @Override
    public Map<String, Object> getItemInfo(Long skuId) {
        //参数校验
        if (skuId == null){
            throw new RuntimeException("商品不存在");
        }
        //返回结果初始化
        Map<String, Object> result = new ConcurrentHashMap<>();
        //查询sku_info的信息
        CompletableFuture<SkuInfo> future1 = CompletableFuture.supplyAsync(() -> {
            SkuInfo skuInfo = itemFeign.getSkuInfo(skuId);
            //判断商品是否存在,若不存在返回空
            if (skuInfo == null || skuInfo.getId()==null){
                return null;
            }
            //商品存在,则保存商品的信息
            result.put("skuInfo", skuInfo);
            return skuInfo;
        },threadPoolExecutor);

        //查询分类的信息
        CompletableFuture<Void> future2 = future1.thenAcceptAsync((skuInfo) -> {
            //判断商品是否存在
            if (skuInfo == null){
                return;
            }
            Long category3Id = skuInfo.getCategory3Id();
            BaseCategoryView baseCategoryView = itemFeign.getCategory(category3Id);
            result.put("baseCategoryView", baseCategoryView);
        },threadPoolExecutor);

        //查询图片的信息
        CompletableFuture<Void> future3 = future1.thenAcceptAsync((skuInfo) -> {
            //判断商品是否存在
            if (skuInfo == null){
                return;
            }
            List<SkuImage> imageList = itemFeign.getImageList(skuInfo.getId());
            result.put("imageList", imageList);
        },threadPoolExecutor);

        //查询价格的信息
        CompletableFuture<Void> future4 = future1.thenAcceptAsync((skuInfo) -> {
            //判断商品是否存在
            if (skuInfo == null){
                return;
            }
            BigDecimal price = itemFeign.getPrice(skuInfo.getId());
            result.put("price", price);
        },threadPoolExecutor);

        //查询销售属性的信息
        CompletableFuture<Void> future5 = future1.thenAcceptAsync((skuInfo) -> {
            //判断商品是否存在
            if (skuInfo == null){
                return;
            }
            List<SpuSaleAttr> spuSaleAttrList = itemFeign.getSpuSaleAttr(skuInfo.getSpuId(), skuInfo.getId());
            result.put("spuSaleAttrList", spuSaleAttrList);
        },threadPoolExecutor);

        //查询skuid和sku拥有的销售属性值的键值对,页面跳转
        CompletableFuture<Void> future6 = future1.thenAcceptAsync((skuInfo -> {
            //判断商品是否存在
            if (skuInfo == null){
                return;
            }
            Map skuIdAndValues = itemFeign.getSkuIdAndValues(skuInfo.getSpuId());
            result.put("skuIdAndValues", skuIdAndValues);
        }),threadPoolExecutor);

        //等到所有的任务都执行完才能返回
        CompletableFuture.allOf(future1, future2, future3, future4, future5, future6).join();
        //将以上查询的结果整合到一起,返回
        return result;
    }
}
