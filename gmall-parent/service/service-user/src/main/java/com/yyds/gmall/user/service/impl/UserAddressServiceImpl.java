package com.yyds.gmall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yyds.gmall.model.user.UserAddress;
import com.yyds.gmall.user.mapper.UserAddressMapper;
import com.yyds.gmall.user.service.UserAddressService;
import com.yyds.gmall.user.util.UserThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: UserAddressServiceImpl
 * @Author: yyd
 * @Date: 2022/8/8/008
 * @Description: 用户收货地址的接口类的实现类
 */
@Service
public class UserAddressServiceImpl implements UserAddressService {
    @Autowired
    private UserAddressMapper userAddressMapper;
    /**
     * 查询收货地址信息
     *
     * @return
     */
    @Override
    public List<UserAddress> getUserAddress() {
        String username = UserThreadLocalUtil.get();
        return userAddressMapper.selectList(
                new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getUserId,username));
    }
}
