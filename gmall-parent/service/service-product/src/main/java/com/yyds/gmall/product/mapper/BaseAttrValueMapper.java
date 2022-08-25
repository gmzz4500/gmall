package com.yyds.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yyds.gmall.model.product.BaseAttrValue;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName: BaseAttrValueMapper
 * @Author: yyd
 * @Date: 2022/7/26/026
 * @Description:
 */

/**
 * 平台属性值表的mapper映射
 */
@Mapper
public interface BaseAttrValueMapper extends BaseMapper<BaseAttrValue> {
}
