package com.yyds.gmall.payment.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.wxpay.sdk.WXPayUtil;
import com.yyds.gmall.common.util.HttpClient;
import com.yyds.gmall.payment.service.WxPayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: WxPayServiceImpl
 * @Author: yyd
 * @Date: 2022/8/14/014
 * @Description:
 */
@Service
public class WxPayServiceImpl implements WxPayService {
    
    @Value("${weixin.pay.appid}")
    private String appId;
    
    @Value("${weixin.pay.partner}")
    private String partner;
    
    @Value("${weixin.pay.partnerkey}")
    private String partnerKey;
    
    @Value("${weixin.pay.notifyUrl}")
    private String notifyUrl;
    
    /**
     * 获取微信支付的二维码地址
     * @param orderMap
     * @return
     */
    @Override
    public String getPayUrl(Map<String, String> orderMap) {
        //获取统一下单的 api的地址
        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        //包装请求参数
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appid",appId);
        paramMap.put("mch_id", partner);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        paramMap.put("body", orderMap.get("desc"));
        paramMap.put("out_trade_no", orderMap.get("orderId"));
        paramMap.put("total_fee", orderMap.get("money"));
        paramMap.put("spbill_create_ip", "192.168.247.1");
        paramMap.put("notify_url", notifyUrl);
        paramMap.put("trade_type", "NATIVE");
        //附加数据
        Map<String, String> attachMap = new HashMap<>();
        attachMap.put("exchange", orderMap.get("exchange"));
        attachMap.put("routingKey", orderMap.get("routingKey"));
        //判断用户名是否为空
        if(StringUtils.isEmpty(orderMap.get("username"))){
            attachMap.put("username", orderMap.get("username"));
        }
        //保存附加数据
        paramMap.put("attach", JSONObject.toJSONString(attachMap));
        try {
            //将map类型的数据转换为xml同时生成签名
            String paramXml = WXPayUtil.generateSignedXml(paramMap, partnerKey);
            //初始化httpclient客户端对象
            HttpClient httpClient = new HttpClient(url);
            httpClient.setHttps(true);
            httpClient.setXmlParam(paramXml);
            //发起post请求
            httpClient.post();
            //获取返回的结果
            String content = httpClient.getContent();
            //解析结果
            Map<String, String> result = WXPayUtil.xmlToMap(content);
//        //判断通讯标识
//        if (result.get("return_code").equals("SUCCESS")){
//            //判断业务标识
//            if (result.get("result_coda").equals("SUCCESS")){
//                return result.get("code_url");
//            }
//        }
            //获取全部返回结果
            return JSONObject.toJSONString(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 根据订单号查询订单的支付结果
     *
     * @param orderId
     * @return
     */
    @Override
    public String getPayResult(String orderId) {
        //查询订单支付结果的地址
        String url = "https://api.mch.weixin.qq.com/pay/orderquery";
        //包装请求参数
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appid", appId);
        paramMap.put("mch_id", partner);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        paramMap.put("out_trade_no", orderId);
        try {
            //将map类型的数据转换为xml同时生成签名
            String paramXml = WXPayUtil.generateSignedXml(paramMap, partnerKey);
            //初始化httpclient客户端对象
            HttpClient httpClient = new HttpClient(url);
            httpClient.setHttps(true);
            httpClient.setXmlParam(paramXml);
            //发起post请求
            httpClient.post();
            //获取返回结果
            String content = httpClient.getContent();
            //解析结果
            Map<String, String> result = WXPayUtil.xmlToMap(content);
            //            //判断通讯标识
//            if(result.get("return_code").equals("SUCCESS")){
//                //判断业务标识
//                if(result.get("result_code").equals("SUCCESS")){
//                    return result.get("code_url");
//                }
//            }
            //返回全部结果
            return JSONObject.toJSONString(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
