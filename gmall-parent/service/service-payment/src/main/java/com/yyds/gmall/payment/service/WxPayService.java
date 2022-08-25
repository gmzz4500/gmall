package com.yyds.gmall.payment.service;

import java.util.Map;

/**
 * @ClassName: WxPayService
 * @Author: yyd
 * @Date: 2022/8/14/014
 * @Description: 微信支付的接口类
 */
public interface WxPayService {
    /**
     * 获取微信支付的二维码地址
     * @param paramMap
     * @return
     */
    public String getPayUrl(Map<String, String> paramMap);
    
    /**
     * 根据订单号查询订单的支付结果
     * @param orderId
     * @return
     */
    public String getPayResult(String orderId);
}
