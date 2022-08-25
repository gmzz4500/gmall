package com.yyds.gmall.cart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yyds.gmall.model.cart.CartInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @ClassName: CartIfnoMapper
 * @Author: yyd
 * @Date: 2022/8/9/009
 * @Description: 购物车表的mapper映射
 */
@Mapper
public interface CartIfnoMapper extends BaseMapper<CartInfo> {
    /**
     * 修改某个用户的全部购物车选中状态
     * @param username
     * @param status
     * @return
     */
    @Update("update cart_info set is_checked = #{status} where user_id = #{username}")
    public int updateAllCart(@Param("username") String username,
                             @Param("status") Short status);
    
    /**
     * 修改某个用户的单条购物车选中状态
     * @param username
     * @param status
     * @param id
     * @return
     */
    @Update("update cart_info set is_checked = #{status} where user_id = #{username} and id = #{id}")
    public int updateOneCart(@Param("username") String username,
                             @Param("status") Short status,
                             @Param("id") Long id);
}
