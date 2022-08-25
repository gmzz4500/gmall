package com.yyds.gmall.oauth.service.impl;

import com.yyds.gmall.oauth.service.LoginService;
import com.yyds.gmall.oauth.util.AuthToken;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

/**
 * @ClassName: LoginServiceImpl
 * @Author: yyd
 * @Date: 2022/8/8/008
 * @Description:
 */
@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    /**
     * 登录
     *
     * @param username
     * @param password
     * @return
     */
    @Override
    public AuthToken login(String username, String password) {
        //参数校验
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new RuntimeException("用户名和密码不能为空!");
        }
        //地址
        ServiceInstance choose =
                loadBalancerClient.choose("service-oauth");
        String url = choose.getUri().toString() + "/oauth/token";
        //初始化请求头
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.set("Authorization", getHeadParam());
        //初始化请求体
        MultiValueMap<String, String> body = new HttpHeaders();
        body.set("username",username);
        body.set("password",password);
        body.set("grant_type", "password");
        //构建参数
        HttpEntity httpEntity = new HttpEntity(body,headers);
        String response = restTemplate.getForObject(url, String.class);
        //发起post请求
        /**
         * 1.地址
         *2.请求方式
         * 3.请求参数
         * 4.返回结果的类型
         */
        ResponseEntity<Map> exchange = 
                restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
        //获取响应的数据
        Map<String, String> result = exchange.getBody();
        AuthToken authToken = new AuthToken();
        //获取令牌的信息并且包装
        authToken.setJti(result.get("jti"));
        authToken.setAccessToken(result.get("access_token"));
        authToken.setRefreshToken(result.get("refresh_token"));
        //返回
        return authToken;
    }
    @Value("${auth.clientId}")
    private String clientId;
    @Value("${auth.clientSecret}")
    private String clientSecret;
    
    /**
     * 拼接请求头中的参数
     * @return
     */
    private String getHeadParam(){
        //明文拼接
        String head = clientId + ":" + clientSecret;
        //base64加密
        byte[] encode = Base64.getEncoder().encode(head.getBytes());
        //返回结果
        return "Basic " + new String(encode);
    }
}
