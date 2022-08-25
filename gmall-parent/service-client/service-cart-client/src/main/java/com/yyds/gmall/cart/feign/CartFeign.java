package com.yyds.gmall.cart.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * @ClassName: CartFeign
 * @Author: yyd
 * @Date: 2022/8/10/010
 * @Description: 购物车提供的内部调用接口
 */
@FeignClient(name = "service-cart", path = "/cart", contextId = "CartFeign")
public interface CartFeign {
    
    /**
     * 下单时候获取购物车的相关信息
     * @return
     */
    @GetMapping(value = "/getCartInfo")
    public Map<String, Object> getCartInfo();
    
    /**
     * 下单后清理购买的购物车数据
     */
    @GetMapping(value = "/clearCart")
    public void clearCart();
}
