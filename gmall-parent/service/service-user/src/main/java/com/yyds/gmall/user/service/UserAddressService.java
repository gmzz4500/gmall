package com.yyds.gmall.user.service;

import com.yyds.gmall.model.user.UserAddress;

import java.util.List;

/**
 * @ClassName: UserAddressService
 * @Author: yyd
 * @Date: 2022/8/8/008
 * @Description: 用户收货地址的mapper映射
 */
public interface UserAddressService {
    /**
     * 查询收货地址信息
     * @return
     */
    public List<UserAddress> getUserAddress();
}
