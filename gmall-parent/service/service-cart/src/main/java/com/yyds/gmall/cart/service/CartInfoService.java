package com.yyds.gmall.cart.service;

import com.yyds.gmall.model.cart.CartInfo;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: CartInfoService
 * @Author: yyd
 * @Date: 2022/8/9/009
 * @Description: 购物车微服务的接口类
 */
public interface CartInfoService {
    /**
     * 新增购物车
     * @param skuId
     * @param skuNum
     */
    public void addCart(Long skuId,Integer skuNum);
    
    /**
     * 查询登录用户的购物车列表
     *
     * @return
     */
    public List<CartInfo> getCartList();
    
    /**
     * 删除购物车
     * @param cartId
     */
    public void removeCart(Long cartId);
    
    /**
     * 选中状态修改
     * @param status
     * @param cartId
     */
    public void checkOrUnCheck(Short status, Long cartId);
    
    /**
     * 获取用户本次要购买的购物车信息
     *
     * @return
     */
    public Map<String, Object> getOrderConfirm();
    
    /**
     * 清空本次购买的购物项
     */
    public void clearCart();
}
