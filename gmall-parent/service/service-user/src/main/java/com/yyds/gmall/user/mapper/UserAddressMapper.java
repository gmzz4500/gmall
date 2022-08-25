package com.yyds.gmall.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yyds.gmall.model.user.UserAddress;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName: UserAddressMapper
 * @Author: yyd
 * @Date: 2022/8/8/008
 * @Description:
 */
@Mapper
public interface UserAddressMapper extends BaseMapper<UserAddress> {

}
