package com.yyds.gmall.seckill.controller;

import com.yyds.gmall.common.result.Result;
import com.yyds.gmall.seckill.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SeckillOrderController
 * @Author: yyd
 * @Date: 2022/8/15/015
 * @Description:秒杀下单的控制层
 */
@RestController
@RequestMapping(value = "/api/seckill/order")
public class SeckillOrderController {
    
    @Autowired
    private SeckillOrderService seckillOrderService;
    
    /**
     * 秒杀排队
     * @param time
     * @param goodsId
     * @param num
     * @return
     */
    @GetMapping(value = "/addSeckillOrder")
    public Result addSeckillOrder(String time, String goodsId, Integer num) {
        return Result.ok(seckillOrderService.addSeckillOrder(time, goodsId, num));
    }
    
    /**
     * 查询用户的排队状态
     * @return
     */
    @GetMapping(value = "/getUserRecode")
    public Result getUserRecode(){
        return Result.ok(seckillOrderService.getUserRecode());
    }
}
