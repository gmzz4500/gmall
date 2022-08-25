package com.yyds.gmall.product.service;

/**
 * @ClassName: ManageService
 * @Author: yyd
 * @Date: 2022/7/27/027
 * @Description:
 */

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yyds.gmall.model.product.*;

import java.util.List;

/***
 *后台管理使用的接口类
 */
public interface ManageService {
    /**
     * 查询所有的一级分类
     *
     * @return
     */
    public List<BaseCategory1> getCategory1();

    /**
     * 根据一级分类id查询所有的二级分类
     *
     * @param c1Id
     * @return
     */
    public List<BaseCategory2> getCategory2(Long c1Id);

    /**
     * 根据二级分类id查询所有的三级分类
     *
     * @param c2Id
     * @return
     */
    public List<BaseCategory3> getCategory3(Long c2Id);

    /**
     * 保存平台属性
     *
     * @param baseAttrInfo
     */
    public void saveBaseAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 根据分类的id查询分类的平台属性列表
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    public List<BaseAttrInfo> getBaseAttrInfo(Long category1Id,
                                              Long category2Id,
                                              Long category3Id);

    /**
     * 根据平台属性id查询平台属性值列表
     * @param attrId
     * @return
     */
    public List<BaseAttrValue> getBaseAttrValue(Long attrId);

    /**
     * 查询所有的品牌列表
     * @return
     */
    public List<BaseTrademark> getBaseTrademark();

    /**
     * 查询单个品牌
     * @return
     */
    public BaseTrademark getTrademark(Long tmId);

    /**
     * 分页查询品牌列表
     * @param page
     * @param size
     * @return
     */
    public IPage<BaseTrademark> baseTrademark(Integer page, Integer size);

    /**
     * 添加品牌
     * @param baseTrademark
     */
    public void saveBaseTrademark(BaseTrademark baseTrademark);

    /**
     * 修改品牌
     * @param baseTrademark
     */
    public void updateBaseTrademark(BaseTrademark baseTrademark);

    /**
     * 删除品牌
     * @param tmId
     */
    public void deleteBaseTrademark(Long tmId);

    /**
     * 查询所有的基础属性列表
     * @return
     */
    public List<BaseSaleAttr> getBaseSaleAttr();

    /**
     * 保存Spu的信息和修改spu的信息
     * @param spuInfo
     */
    public void saveOrUpdateSpuInfo(SpuInfo spuInfo);

    /**
     * 分页条件查询
     * @param page
     * @param size
     * @param category3Id
     * @return
     */
    public IPage<SpuInfo> pageSpuInfo(Integer page, Integer size, Long category3Id);

    /**
     * 根据spu的id查询spu的销售属性名称和销售属性值的列表
     * @param spuId
     * @return
     */
    public List<SpuSaleAttr> getSpuSaleAttr(Long spuId);

    /**
     * 根据spu的id查询图片列表
     * @param spuId
     * @return
     */
    public List<SpuImage> getSpuImage(Long spuId);

    /**
     * 新增或者修改sku的信息
     * @param skuInfo
     */
    public void SaveOrUpdateSkuInfo(SkuInfo skuInfo);

    /**
     * 分页查询sku的信息
     * @param page
     * @param size
     * @return
     */
    public IPage<SkuInfo> pageSkuInfo(Integer page, Integer size);

    /**
     * 上架或下架
     * @param skuId
     * @param status
     */
    public void upOrDown(Long skuId,Short status);

}
