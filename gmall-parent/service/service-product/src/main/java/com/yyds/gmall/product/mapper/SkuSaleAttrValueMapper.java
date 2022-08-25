package com.yyds.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yyds.gmall.model.product.SkuSaleAttrValue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SkuSaleAttrValueMapper
 * @Author: yyd
 * @Date: 2022/7/29/029
 * @Description:
 */

/***
 * SkuSaleAttrValue表的mapper映射
 */
@Mapper
public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {
    /**
     * 根据spu的id查询这个spu下所有sku的id和拥有销售属性值的键值对
     * @param spuId
     * @return
     */
    public List<Map> selectSaleAttrKeyValueBySpuId(@Param("spuId") Long spuId);
}
