package com.yyds.gmall.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yyds.gmall.seckill.pojo.SeckillOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName: SeckillOrderMapper
 * @Author: yyd
 * @Date: 2022/8/15/015
 * @Description: 秒杀订单表的mapper映射
 */
@Mapper
public interface SeckillOrderMapper extends BaseMapper<SeckillOrder> {
}
