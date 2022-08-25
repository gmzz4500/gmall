package com.yyds.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yyds.gmall.model.product.SpuImage;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName: SpuImageMapper
 * @Author: yyd
 * @Date: 2022/7/29/029
 * @Description:
 */

/***
 * spu图片表的mapper映射
 */
@Mapper
public interface SpuImageMapper extends BaseMapper<SpuImage> {
}
