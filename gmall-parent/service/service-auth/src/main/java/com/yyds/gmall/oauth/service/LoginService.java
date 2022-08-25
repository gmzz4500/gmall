package com.yyds.gmall.oauth.service;

import com.yyds.gmall.oauth.util.AuthToken;

/**
 * @ClassName: LoginService
 * @Author: yyd
 * @Date: 2022/8/8/008
 * @Description: 用户登录的接口类
 */
public interface LoginService {
    /**
     * 登录
     *
     * @param username
     * @param password
     * @return
     */
    public AuthToken login(String username, String password);
}
