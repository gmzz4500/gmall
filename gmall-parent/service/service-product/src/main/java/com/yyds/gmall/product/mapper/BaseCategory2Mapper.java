package com.yyds.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yyds.gmall.model.product.BaseCategory2;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName: BaseCategory2Mapper
 * @Author: yyd
 * @Date: 2022/7/27/027
 * @Description:
 */

/***
 * 根据一级分类查询二级分类
 */
@Mapper
public interface BaseCategory2Mapper extends BaseMapper<BaseCategory2> {
}
