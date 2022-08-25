package com.yyds.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yyds.gmall.model.product.SpuInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName: SpuInfoMapper
 * @Author: yyd
 * @Date: 2022/7/29/029
 * @Description:
 */

/***
 * spuInfo表的mapper映射
 */
@Mapper
public interface SpuInfoMapper extends BaseMapper<SpuInfo> {
}
