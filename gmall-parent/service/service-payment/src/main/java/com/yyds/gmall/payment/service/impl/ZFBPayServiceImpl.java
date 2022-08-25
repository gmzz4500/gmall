package com.yyds.gmall.payment.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.yyds.gmall.payment.service.ZFBPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 支付宝支付的接口类的实现类
 */
@Service
public class ZFBPayServiceImpl implements ZFBPayService {

    @Autowired
    private AlipayClient alipayClient;
    
    @Value("${ali.alipayUrl}")
    private String alipayUrl;
    
    @Value("${ali.appId}")
    private String appId;
    
    @Value("${ali.appPrivateKey}")
    private String appPrivateKey;
    
    @Value("${ali.alipayPublicKey}")
    private String alipayPublicKey;
    
    @Value("${ali.returnPaymentUrl}")
    private String returnPaymentUrl;
    
    @Value("${ali.notifyPaymentUrl}")
    private String notifyPaymentUrl;
    /**
     * 获取支付宝支付的页面
     *
     * @param desc
     * @param orderId
     * @param money
     * @return
     */
    @Override
    public String getPayPage(String desc, String orderId, String money) {
        //请求体初始化
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        //设置异步通知的地址
        request.setNotifyUrl(notifyPaymentUrl);
        //设置同步回调的地址
        request.setReturnUrl(returnPaymentUrl);
        //构建请求参数
        JSONObject bizContent = new JSONObject();
        //订单号
        bizContent.put("out_trade_no", orderId);
        //金额
        bizContent.put("total_amount", money);
        //订单描述
        bizContent.put("subject", desc);
        //产品码
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        //设置附加参数
        request.setBizContent(bizContent.toString());
        try {
            //发起请求
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            //返回
            return response.getBody();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询支付结果
     *
     * @param orderId
     * @return
     */
    @Override
    public String getPayResult(String orderId) {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderId);
        request.setBizContent(bizContent.toString());
        try {
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            return response.getBody();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
