package com.yyds.gmall.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yyds.gmall.model.order.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName: OrderMapper
 * @Author: yyd
 * @Date: 2022/8/10/010
 * @Description: 订单详情表的mapper映射
 */
@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {
}
