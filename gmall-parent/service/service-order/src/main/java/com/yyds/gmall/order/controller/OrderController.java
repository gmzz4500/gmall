package com.yyds.gmall.order.controller;

import com.yyds.gmall.common.result.Result;
import com.yyds.gmall.model.order.OrderInfo;
import com.yyds.gmall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName: OrderController
 * @Author: yyd
 * @Date: 2022/8/10/010
 * @Description:
 */
@RestController
@RequestMapping(value = "/api/order")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 新增普通订单
     *
     * @param orderInfo
     * @return
     */
    @PostMapping(value = "/addOrder")
    public Result addOrder(@RequestBody OrderInfo orderInfo) {
        orderService.addOrder(orderInfo);
        return Result.ok();
    }
    
    @Autowired
    private RedisTemplate redisTemplate;
    
    /**
     * 主动取消
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/cancelOrder")
    public Result cancelOrder(Long orderId) {
        //使用用户名增值,每次加1
        Long increment = redisTemplate
                .opsForValue()
                .increment("user_cancle_order_count_" + orderId, 1);
        redisTemplate.expire("user_cancle_order_count_" + orderId, 5, TimeUnit.SECONDS);
        if (increment > 1) {
            return Result.ok();
        }
        try {
            orderService.cancelOrder(orderId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisTemplate.delete("user_cancel_order_count_" + orderId);
        }
        return Result.ok();
    }
}
