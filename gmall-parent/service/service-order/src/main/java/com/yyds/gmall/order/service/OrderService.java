package com.yyds.gmall.order.service;

import com.yyds.gmall.model.order.OrderInfo;

/**
 * @ClassName: OrderService
 * @Author: yyd
 * @Date: 2022/8/10/010
 * @Description: 普通订单的接口类
 */
public interface OrderService {
    /**
     * 新增订单
     * @param orderInfo
     */
    public void addOrder(OrderInfo orderInfo);
    
    /**
     * 取消订单
     * @param orderId
     */
    public void cancelOrder(Long orderId);
    
    /**
     * 修改订单的支付状态
     * @param result
     */
    public void updateOrder(String result);
    
}

