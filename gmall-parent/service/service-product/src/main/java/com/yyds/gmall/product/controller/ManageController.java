package com.yyds.gmall.product.controller;

/**
 * @ClassName: ManageController
 * @Author: yyd
 * @Date: 2022/7/27/027
 * @Description:
 */

import com.yyds.gmall.common.constant.ProductConst;
import com.yyds.gmall.common.result.Result;
import com.yyds.gmall.model.product.BaseAttrInfo;
import com.yyds.gmall.model.product.BaseTrademark;
import com.yyds.gmall.model.product.SkuInfo;
import com.yyds.gmall.model.product.SpuInfo;
import com.yyds.gmall.product.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/***
 * 后台管理页面的控制层
 */
@RestController
@RequestMapping(value = "/admin/product")
public class ManageController {
    @Autowired
    private ManageService manageService;

    /**
     * 查询所有的一级分类
     *
     * @return
     */
    @GetMapping(value = "/getCategory1")
    public Result getCategory1() {
        return Result.ok(manageService.getCategory1());
    }

    /**
     * 根据一级分类id查询所有二级分类
     *
     * @param c1Id
     * @return
     */
    @GetMapping(value = "/getCategory2/{c1Id}")
    public Result getCategory2(@PathVariable Long c1Id) {
        return Result.ok(manageService.getCategory2(c1Id));
    }

    /**
     * 根据二级分类id查询所有三级分类
     *
     * @param c2Id
     * @return
     */
    @GetMapping(value = "/getCategory3/{c2Id}")
    public Result getCategory3(@PathVariable Long c2Id) {
        return Result.ok(manageService.getCategory3(c2Id));
    }

    /**
     * 保存平台属性
     *
     * @param baseAttrInfo
     * @return
     */
    @PostMapping(value = "/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo) {
        manageService.saveBaseAttrInfo(baseAttrInfo);
        return Result.ok();
    }

    /**
     * 根据分类的id查询分类的平台属性列表
     *
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @GetMapping(value = "/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoList(@PathVariable(value = "category1Id") Long category1Id,
                               @PathVariable(value = "category2Id") Long category2Id,
                               @PathVariable(value = "category3Id") Long category3Id) {
        return Result.ok(manageService.getBaseAttrInfo(category1Id, category2Id, category3Id));
    }

    /**
     * 查询平台属性值列表
     * @param attrId
     * @return
     */
    @GetMapping(value = "/getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable(value = "attrId") Long attrId){
        return Result.ok(manageService.getBaseAttrValue(attrId));
    }

    /**
     * 查询所有的品牌列表
     * @return
     */
    @GetMapping(value = "/baseTrademark/getTrademarkList")
    public Result getTrademarkList(){
        return Result.ok(manageService.getBaseTrademark());
    }

    /**
     * 查询单个品牌信息
     * @param tmId
     * @return
     */
    @GetMapping(value = "/baseTrademark/get/{tmId}")
    public Result getTrademark(@PathVariable Long tmId){
        return Result.ok(manageService.getTrademark(tmId));
    }

    /**
     * 分页查询品牌列表
     * @param page
     * @param size
     * @return
     */
    @GetMapping(value = "/baseTrademark/{page}/{size}")
    public Result baseTrademark(@PathVariable(value = "page") Integer page,
                                @PathVariable(value = "size") Integer size){
        return Result.ok(manageService.baseTrademark(page, size));
    }

    /**
     * 添加品牌
     * @param baseTrademark
     * @return
     */
    @PostMapping(value = "/baseTrademark/save")
    public Result saveBaseTrademark(@RequestBody BaseTrademark baseTrademark){
        manageService.saveBaseTrademark(baseTrademark);
        return Result.ok();
    }

    /**
     * 修改品牌
     * @param baseTrademark
     * @return
     */
    @PutMapping(value = "/baseTrademark/update")
    public Result updateBaseTrademark(@RequestBody BaseTrademark baseTrademark){
        manageService.updateBaseTrademark(baseTrademark);
        return Result.ok();
    }

    /**
     * 删除品牌
     * @param tmId
     * @return
     */
    @DeleteMapping(value = "/baseTrademark/remove/{tmId}")
    public Result deleteBaseTrademark(@PathVariable Long tmId){
        manageService.deleteBaseTrademark(tmId);
        return Result.ok();
    }

    /**
     * 查询所有的销售属性
     * @return
     */
    @GetMapping(value = "/baseSaleAttrList")
    public Result baseSaleAttrList(){
        return Result.ok(manageService.getBaseSaleAttr());
    }

    /**
     * 保存Spu的信息和修改spu的信息
     * @param spuInfo
     * @return
     */
    @PostMapping(value = "/saveSpuInfo")
    public Result saveOrUpdateSpuInfo(@RequestBody SpuInfo spuInfo){
        manageService.saveOrUpdateSpuInfo(spuInfo);
        return Result.ok();
    }

    /**
     * 分页条件查询spu的信息
     * @param page
     * @param size
     * @param category3Id
     * @return
     */
    @GetMapping(value = "/{page}/{size}")
    public Result pageSpuInfo(@PathVariable(value = "page") Integer page,
                              @PathVariable(value = "size") Integer size,
                              Long category3Id){
        return Result.ok(manageService.pageSpuInfo(page, size, category3Id));
    }

    /**
     * 查询指定spu的销售属性信息
     * @param spuId
     * @return
     */
    @GetMapping(value = "/spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable(value = "spuId") Long spuId){
        return Result.ok(manageService.getSpuSaleAttr(spuId));
    }

    /**
     * 查询指定spu的图片列表信息
     * @param spuId
     * @return
     */
    @GetMapping(value = "/spuImageList/{spuId}")
    public Result spuImageList(@PathVariable(value = "spuId") Long spuId){
        return Result.ok(manageService.getSpuImage(spuId));
    }

    /**
     * 保存或者修改sku的信息
     * @param skuInfo
     * @return
     */
    @PostMapping(value = "/saveSkuInfo")
    public Result SaveOrUpdateSkuInfo(@RequestBody SkuInfo skuInfo){
        manageService.SaveOrUpdateSkuInfo(skuInfo);
        return Result.ok();
    }

    /**
     * 分页查询sku的信息
     * @param page
     * @param size
     * @return
     */
    @GetMapping(value = "/list/{page}/{size}")
    public Result pageSkuInfo(@PathVariable(value = "page") Integer page,
                              @PathVariable(value = "size") Integer size){
        return Result.ok(manageService.pageSkuInfo(page, size));
    }

    /**
     * 商品上架
     * @param skuId
     * @return
     */
    @GetMapping(value = "/onSale/{skuId}")
    public Result  onSale(@PathVariable(value = "skuId") Long skuId){
        manageService.upOrDown(skuId, ProductConst.SKU_ON_SALE);
        return Result.ok();

    }
    /**
     * 商品下架
     * @param skuId
     * @return
     */
    @GetMapping(value = "/cancelSale/{skuId}")
    public Result  cancelSale(@PathVariable(value = "skuId") Long skuId){
        manageService.upOrDown(skuId, ProductConst.SKU_CANCEL_SALE);
        return Result.ok();
    }
}
