package com.yyds.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yyds.gmall.model.product.SkuInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @ClassName: SkuInfoMapper
 * @Author: yyd
 * @Date: 2022/7/29/029
 * @Description:
 */

/***
 * skuInfo表的mapper映射
 */
@Mapper
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {
    /**
     * 扣减库存
     *
     * @param skuId
     * @param skuNum
     * @return
     */
    @Update("update sku_info set stock = stock - #{skuNum} where id = #{skuId} and stock >= #{skuNum}")
    public int decountStocks(@Param("skuId") Long skuId,
                             @Param("skuNum") Integer skuNum);
    
    /**
     * 回滚库存
     *
     * @param skuId
     * @param skuNum
     * @return
     */
    @Update("update sku_info set stock = stock + #{skuNum} where id = #{skuId}")
    public int rollbackStocks(@Param("skuId") Long skuId,
                             @Param("skuNum") Integer skuNum);
}
