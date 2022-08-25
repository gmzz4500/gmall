package com.yyds.gmall.cart.controller;

import com.yyds.gmall.cart.service.CartInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @ClassName: Cart4InternalCalls
 * @Author: yyd
 * @Date: 2022/8/10/010
 * @Description: 内部调用的控制层
 */
@RestController
@RequestMapping(value = "/cart")
public class Cart4InternalCalls {
    
    @Autowired
    private CartInfoService cartInfoService;
    
    /**
     * 下单时候获取购物车的相关信息
     * @return
     */
    @GetMapping(value = "/getCartInfo")
    public Map<String, Object> getCartInfo(){
        return cartInfoService.getOrderConfirm();
    }
    
    /**
     * 下单后清理购买的购物车数据
     */
    @GetMapping(value = "/clearCart")
    public void clearCart(){
        cartInfoService.clearCart();
    }
}
