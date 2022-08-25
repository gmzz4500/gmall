package com.yyds.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yyds.gmall.model.product.SpuSaleAttr;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName: SpuSaleAttrMapper
 * @Author: yyd
 * @Date: 2022/7/29/029
 * @Description:
 */

/***
 * spu销售属性名称表的映射
 */
@Mapper
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {

    /**
     *根据spu的id查询销售属性的信息
     * @param spuId
     */
    public List<SpuSaleAttr> selectSpuSaleAttrBySpuId(@Param(value = "spuId") Long spuId);

    /**
     * 根据spuid和skuid查询商品的销售属性信息并且标识出当前sku的销售属性是哪几个
     * @param spuId
     * @param skuId
     * @return
     */
    public List<SpuSaleAttr> slectSpuSaleAttrBySpuIdAndSkuId(@Param(value = "spuId") Long spuId,
                                                             @Param(value = "skuId") Long skuId);
}
