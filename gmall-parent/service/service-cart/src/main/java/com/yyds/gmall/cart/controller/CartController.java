package com.yyds.gmall.cart.controller;

import com.yyds.gmall.cart.service.CartInfoService;
import com.yyds.gmall.common.constant.CartConst;
import com.yyds.gmall.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: CartController
 * @Author: yyd
 * @Date: 2022/8/9/009
 * @Description: 购物车相关接口的控制层
 */
@RestController
@RequestMapping(value = "/api/cart")
public class CartController {
    
    @Autowired
    private CartInfoService cartInfoService;
    
    /**
     * 新增购物车
     * @param skuId
     * @param skuNum
     * @return
     */
    @GetMapping(value = "/addCart")
    public Result addCart(Long skuId, Integer skuNum){
        cartInfoService.addCart(skuId, skuNum);
        return Result.ok();
    }
    
    /**
     * 查询登录用户的购物车列表
     * @return
     */
    @GetMapping(value = "/getCartList")
    public Result getCartList(){
        return Result.ok(cartInfoService.getCartList());
    }
    
    /**
     * 删除购物车
     * @param cartId
     * @return
     */
    @GetMapping(value = "/removeCart")
    public Result removeCart(Long cartId){
        cartInfoService.removeCart(cartId);
        return Result.ok();
    }
    
    /**
     * 选中
     * @param id
     * @return
     */
    @GetMapping(value = "/check")
    public Result check(Long id){
        cartInfoService.checkOrUnCheck(CartConst.CART_CHECK, id);
        return Result.ok();
    }
    
    /**
     * 取消选中
     * @param id
     * @return
     */
    @GetMapping(value = "/uncheck")
    public Result uncheck(Long id){
        cartInfoService.checkOrUnCheck(CartConst.CART_UNCHECK, id);
        return Result.ok();
    }
    
    /**
     * 获取本次用户需要购买的购物车数据以及总数量和金额
     * @return
     */
    @GetMapping(value = "/getOrderConfirm")
    public Result getOrderConfirm(){
        return Result.ok(cartInfoService.getOrderConfirm());
    }
}
