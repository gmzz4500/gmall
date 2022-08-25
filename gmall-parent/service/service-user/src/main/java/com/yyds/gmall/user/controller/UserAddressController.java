package com.yyds.gmall.user.controller;

import com.yyds.gmall.common.result.Result;
import com.yyds.gmall.user.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: UserAddressController
 * @Author: yyd
 * @Date: 2022/8/8/008
 * @Description: 用户收货地址的控制层
 */
@RestController
@RequestMapping(value = "/api/user")
public class UserAddressController {
    /**
     * 查询收货地址信息
     */
    @Autowired
    private UserAddressService userAddressService;
    @GetMapping(value = "/getUserAddress")
    public Result getUserAddress(){
        return Result.ok(userAddressService.getUserAddress());
    }
}
