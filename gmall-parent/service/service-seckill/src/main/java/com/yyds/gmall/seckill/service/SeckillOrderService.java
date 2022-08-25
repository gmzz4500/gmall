package com.yyds.gmall.seckill.service;

import com.yyds.gmall.seckill.pojo.UserRecode;

/**
 * @ClassName: SeckillOrderService
 * @Author: yyd
 * @Date: 2022/8/15/015
 * @Description:秒杀下单的接口类
 */
public interface SeckillOrderService {
    /**
     * 秒杀下单
     *
     * @param time
     * @param goodsId
     * @param num
     * @return
     */
    public UserRecode addSeckillOrder(String time, String goodsId, Integer num);
    
    /**
     * 查询用户的排队状态
     * @return
     */
    public UserRecode getUserRecode();
    
    /**
     * 真实下单
     * @param userRecode
     */
    public void realSeckillOrderAdd(UserRecode userRecode) throws Exception;
    
    /**
     * 取消订单
     * @param username
     */
    public void cancelSeckillOrder(String username);
    
    /**
     * 修改秒杀订单的支付结果
     * @param result
     */
    public void updateSeckillOrder(String result);
}
