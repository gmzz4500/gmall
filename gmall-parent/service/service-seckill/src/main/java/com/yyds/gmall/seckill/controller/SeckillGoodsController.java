package com.yyds.gmall.seckill.controller;

import com.yyds.gmall.common.result.Result;
import com.yyds.gmall.seckill.service.SeckillGoodsService;
import com.yyds.gmall.seckill.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SeckillGoodsController
 * @Author: yyd
 * @Date: 2022/8/15/015
 * @Description: 秒杀商品的控制层
 */
@RestController
@RequestMapping(value = "/api/seckill/goods")
public class SeckillGoodsController {
    
    @Autowired
    private SeckillGoodsService seckillGoodsService;
    
    /**
     * 获取时间段菜单
     * @return
     */
    @GetMapping(value = "/getDateTimes")
    public Result getDateTimes(){
        return Result.ok(DateUtil.getDateMenus());
    }
    
    /**
     * 查询秒杀商品列表
     * @param time
     * @return
     */
    @GetMapping(value = "/getSeckillGoods")
    public Result getSeckillGoods(String time){
        return Result.ok(seckillGoodsService.getSeckillGoods(time));
    }
    
    /**
     * 查询具体的商品详情
     * @param time
     * @param goodsId
     * @return
     */
    @GetMapping(value = "/getSeckillGood")
    public Result getSeckillGood(String time, String goodsId){
        return Result.ok(seckillGoodsService.getSeckillGoods(time,goodsId));
    }
}
