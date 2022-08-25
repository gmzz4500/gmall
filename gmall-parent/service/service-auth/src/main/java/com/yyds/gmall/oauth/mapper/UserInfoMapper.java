package com.yyds.gmall.oauth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yyds.gmall.model.user.UserInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName: UserInfoMapper
 * @Author: yyd
 * @Date: 2022/8/8/008
 * @Description: 用户表的mapper映射
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {

}
