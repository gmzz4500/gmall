package com.yyds.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yyds.gmall.model.product.BaseSaleAttr;
import com.yyds.gmall.model.product.BaseTrademark;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName: BaseTradeMarkMapper
 * @Author: yyd
 * @Date: 2022/7/29/029
 * @Description:
 */

/***
 * 基础销售属性表的mapper映射
 */
@Mapper
public interface BaseSaleAttrMapper extends BaseMapper<BaseSaleAttr> {
}
