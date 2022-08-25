package com.yyds.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yyds.gmall.model.product.SkuImage;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName: SkuImageMapper
 * @Author: yyd
 * @Date: 2022/7/29/029
 * @Description:
 */

/***
 * SkuImage表的mapper映射
 */
@Mapper
public interface SkuImageMapper extends BaseMapper<SkuImage> {
}
