package com.yyds.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yyds.gmall.model.product.BaseCategory3;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName: BaseCategory2Mapper
 * @Author: yyd
 * @Date: 2022/7/27/027
 * @Description:
 */

/***
 * 根据二级分类查询三级分类
 */
@Mapper
public interface BaseCategory3Mapper extends BaseMapper<BaseCategory3> {
}
