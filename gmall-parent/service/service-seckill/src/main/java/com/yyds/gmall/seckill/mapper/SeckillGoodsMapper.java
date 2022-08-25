package com.yyds.gmall.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yyds.gmall.model.activity.SeckillGoods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @ClassName: SeckillGoodsMapper
 * @Author: yyd
 * @Date: 2022/8/15/015
 * @Description: 秒杀商品表的mapper映射
 */
@Mapper
public interface SeckillGoodsMapper extends BaseMapper<SeckillGoods> {
    /**
     * 同步商品的库存
     * @param goodsId
     * @param stockCount
     * @return
     */
    @Update("update seckill_goods set stock_count = #{stockCount} where id = #{goodsId}")
    public int updateSeckillGoodsStock(@Param("goodsId") Long goodsId,
                                       @Param("stockCount") Integer stockCount);
}
