package com.yyds.gmall.payment.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.wxpay.sdk.WXPayUtil;
import com.yyds.gmall.payment.service.WxPayService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: WxPayController
 * @Author: yyd
 * @Date: 2022/8/14/014
 * @Description: 微信支付的控制层
 */
@RestController
@RequestMapping(value = "/wx/pay")
public class WxPayController {
    @Autowired
    private WxPayService wxPayService;
    
    /**
     * 获取微信支付的二维码地址
     * @param paramMap
     * @return
     */
    @GetMapping(value = "/getPayUrl")
    public String getPayUrl(@RequestParam Map<String, String> paramMap) {
        return wxPayService.getPayUrl(paramMap);
    }
    
    /**
     * 获取订单的支付结果
     * @param orderId
     * @return
     */
    @GetMapping(value = "getPayResult")
    public String getPayResult(String orderId){
        return wxPayService.getPayResult(orderId);
    }
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    /**
     * 给微信调用的获取支付结果的异步通知接口
     * @param request
     * @return
     */
    @RequestMapping(value = "/callback/notify")
    public String wxNotify(HttpServletRequest request)throws Exception{
        //获取微信支付通知的数据流
        ServletInputStream inputStream = request.getInputStream();
        //读取数据流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //定义缓冲区
        byte[] buffer = new byte[1024];
        //定义数据长度
        int len = 0;
        //读取数据
        while ((len = inputStream.read(buffer)) != -1){
            outputStream.write(buffer, 0, len);
        }
        //获取输出流的字节码
        byte[] bytes = outputStream.toByteArray();
        //将字节码转换为字符串
        String xmlString = new String(bytes);
        //将xml转为为map
        Map<String, String> result = WXPayUtil.xmlToMap(xmlString);
        //获取附加参数
        String attachString = result.get("attach");
        //反序列化
        Map<String, String> attach = JSONObject.parseObject(attachString, Map.class);
        //存储支付渠道:1-微信支付
        result.put("payway","1");
        System.out.println(JSONObject.toJSONString(result));
//        String result = "{\"transaction_id\":\"4200001563202208143538590924\",\"nonce_str\":\"669052cd720f40b4bcb8f88b9125573f\",\"bank_type\":\"OTHERS\",\"openid\":\"oHwsHuPbh59MkxPs3He9Z9byiviw\",\"sign\":\"C6195E0074F1BC9580B9660B73D2C794\",\"fee_type\":\"CNY\",\"mch_id\":\"1558950191\",\"cash_fee\":\"1\",\"out_trade_no\":\"252\",\"appid\":\"wx74862e0dfcf69954\",\"total_fee\":\"1\",\"trade_type\":\"NATIVE\",\"result_code\":\"SUCCESS\",\"time_end\":\"20220814163020\",\"is_subscribe\":\"N\",\"return_code\":\"SUCCESS\",\"payway\":\"1\"}\n";
        //将支付的结果发送mq消息给订单微服务--TODO---存在耦合
        rabbitTemplate.convertAndSend(
                attach.get("exchange"),
                attach.get("routingKey"),
                JSONObject.toJSONString(result));
        //返回微信,防止一直调用
        Map<String, String> wxResult = new HashMap<>();
        wxResult.put("return_code", "SUCCESS");
        wxResult.put("return_msg", "OK");
        return WXPayUtil.mapToXml(wxResult);
        
        
    }
}
