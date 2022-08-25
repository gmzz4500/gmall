package com.yyds.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yyds.gmall.model.product.SpuSaleAttrValue;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName: SpuSaleAttrValueMapper
 * @Author: yyd
 * @Date: 2022/7/29/029
 * @Description:
 */

/***
 * spu销售属性值表的mapper映射
 */
@Mapper
public interface SpuSaleAttrValueMapper extends BaseMapper<SpuSaleAttrValue> {
}
