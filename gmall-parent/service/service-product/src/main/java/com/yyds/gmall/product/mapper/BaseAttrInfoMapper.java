package com.yyds.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yyds.gmall.model.product.BaseAttrInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName: SelectById
 * @Author: yyd
 * @Date: 2022/7/26/026
 * @Description:
 */

/**
 * 平台属性表的mapper映射
 */
@Mapper
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {

    /**
     * 根据分类的id查询分类的平台属性列表
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    public List<BaseAttrInfo> selectBaseAttrInfoByCategoryId(@Param("category1Id") Long category1Id,
                                                             @Param("category2Id") Long category2Id,
                                                             @Param("category3Id") Long category3Id);

    /**
     * 查询指定sku的平台属性
     * @param skuId
     * @return
     */
    public List<BaseAttrInfo> selectBaseAttrInfoBySkuId(@Param("skuId") Long skuId);
}
