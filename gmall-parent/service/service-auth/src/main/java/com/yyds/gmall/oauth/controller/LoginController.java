package com.yyds.gmall.oauth.controller;

import com.yyds.gmall.common.util.IpUtil;
import com.yyds.gmall.oauth.service.LoginService;
import com.yyds.gmall.oauth.util.AuthToken;
import com.yyds.gmall.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName: LoginController
 * @Author: yyd
 * @Date: 2022/8/8/008
 * @Description:
 */
@RestController
@RequestMapping(value = "/user/login")
public class LoginController {
    
    @Autowired
    private LoginService loginService;
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @GetMapping
    public Result login(String username,
                        String password,
                        HttpServletRequest request){
        //登录
        AuthToken authToken = loginService.login(username, password);
        //将令牌和ip地址绑定,防止盗用,存储到redis
        String ipAddress = IpUtil.getIpAddress(request);
        //存储在redis
        stringRedisTemplate.opsForValue().set(ipAddress,authToken.getAccessToken());
        //返回
        return Result.ok(authToken);
    }
}
