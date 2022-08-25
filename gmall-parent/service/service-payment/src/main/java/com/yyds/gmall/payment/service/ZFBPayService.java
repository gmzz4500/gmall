package com.yyds.gmall.payment.service;

/**
 * 支付宝支付的接口类
 */
public interface ZFBPayService {

    /**
     * 获取支付宝支付的页面
     * @param desc
     * @param orderId
     * @param money
     * @return
     */
    public String getPayPage(String desc, String orderId, String money);

    /**
     * 查询支付结果
     * @param orderId
     * @return
     */
    public String getPayResult(String orderId);

}