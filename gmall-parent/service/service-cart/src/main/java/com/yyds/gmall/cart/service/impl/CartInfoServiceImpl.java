package com.yyds.gmall.cart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.util.concurrent.AtomicDouble;
import com.yyds.gmall.cart.mapper.CartIfnoMapper;
import com.yyds.gmall.cart.service.CartInfoService;
import com.yyds.gmall.cart.util.CartThreadLocalUtil;
import com.yyds.gmall.common.constant.CartConst;
import com.yyds.gmall.model.cart.CartInfo;
import com.yyds.gmall.model.product.SkuInfo;
import com.yyds.gmall.product.feign.ItemFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @ClassName: CartInfoServiceImpl
 * @Author: yyd
 * @Date: 2022/8/9/009
 * @Description:
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CartInfoServiceImpl implements CartInfoService {
    @Autowired
    private ItemFeign itemFeign;
    @Autowired
    private CartIfnoMapper cartIfnoMapper;
    
    /**
     * 新增购物车
     *
     * @param skuId
     * @param skuNum
     * @return
     */
    @Override
    public void addCart(Long skuId, Integer skuNum) {
        //参数校验
        if (skuId == null || skuNum == null) {
            throw new RuntimeException("参数错误,新增购物车失败");
        }
        //查询商品的数据,判断商品是否存在
        SkuInfo skuInfo = itemFeign.getSkuInfo(skuId);
        if (skuInfo == null || skuInfo.getId() == null) {
            throw new RuntimeException("商品不存在,新增购物车失败");
        }
        //从本地线程对象获取用户名
        String username = CartThreadLocalUtil.get();
        //判断购物车中是否已经存在这个商品
        CartInfo cartInfo = cartIfnoMapper.selectOne(new LambdaQueryWrapper<CartInfo>().eq(CartInfo::getUserId, username).eq(CartInfo::getSkuId, skuId));
        if (cartInfo == null || cartInfo.getId() == null) {
            //购物车中没有这个商品,新增
            if (skuNum <= 0) {
                return;
            }
            //包装购物车对象
            cartInfo = new CartInfo();
            cartInfo.setUserId(username);
            cartInfo.setSkuId(skuId);
            //查询实时价格
            BigDecimal price = itemFeign.getPrice(skuId);
            cartInfo.setCartPrice(price);
            cartInfo.setSkuNum(skuNum);
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo.setSkuName(skuInfo.getSkuName());
            //保存购物车对象
            int insert = cartIfnoMapper.insert(cartInfo);
            if (insert <= 0) {
                throw new RuntimeException("新增购物车失败");
            }
        } else {
            skuNum = cartInfo.getSkuNum() + skuNum;
            if (skuNum <= 0) {
                int deleteById = cartIfnoMapper.deleteById(cartInfo.getId());
                if (deleteById < 0) {
                    throw new RuntimeException("新增购物车失败");
                }
                return;
            }
            //购物车中有这个商品,合并数量
            cartInfo.setSkuNum(skuNum);
            //更新
            int update = cartIfnoMapper.updateById(cartInfo);
            if (update < 0) {
                throw new RuntimeException("新增购物车失败");
            }
        }
        
    }
    
    /**
     * 查询登录用户的购物车列表
     *
     * @return
     */
    @Override
    public List<CartInfo> getCartList() {
        return cartIfnoMapper.selectList(new LambdaQueryWrapper<CartInfo>().eq(CartInfo::getUserId, CartThreadLocalUtil.get()));
    }
    
    /**
     * 删除购物车
     *
     * @param cartId
     */
    @Override
    public void removeCart(Long cartId) {
        cartIfnoMapper.delete(new LambdaQueryWrapper<CartInfo>().eq(CartInfo::getId, cartId).eq(CartInfo::getUserId, CartThreadLocalUtil.get()));
    }
    
    /**
     * 选中状态修改
     *
     * @param status
     * @param cartId
     */
    @Override
    public void checkOrUnCheck(Short status, Long cartId) {
        String username = CartThreadLocalUtil.get();
        int i = 0;
        //判断到底是修改一条还是多条
        if (cartId == null) {
            //全部改
            i = cartIfnoMapper.updateAllCart(username, status);
        } else {
            //单条改
            i = cartIfnoMapper.updateOneCart(username, status, cartId);
        }
        if (i < 0) {
            throw new RuntimeException("修改选中状态失败");
        }
    }
    
    /**
     * 获取用户本次要购买的购物车信息
     *
     * @return
     */
    @Override
    public Map<String, Object> getOrderConfirm() {
        //本次购买的购物车列表
        List<CartInfo> cartInfoList = cartIfnoMapper.selectList(
                new LambdaQueryWrapper<CartInfo>()
                        .eq(CartInfo::getUserId, CartThreadLocalUtil.get())
                        .eq(CartInfo::getIsChecked, CartConst.CART_CHECK));
        //用户至少选中了一个购物车项
        if (!cartInfoList.isEmpty()) {
            //返回结果初始化
            Map<String, Object> result = new HashMap<>();
            //定义总数量
            AtomicInteger totalNum = new AtomicInteger(0);
            //定义总金额
            AtomicDouble totalMoney = new AtomicDouble(0);
            //遍历查询每笔购物车中商品的实时价格
            List<CartInfo> cartInfoListNew = cartInfoList.stream().map(cartInfo -> {
                //查询实时价格
                BigDecimal price = itemFeign.getPrice(cartInfo.getSkuId());
                //保存实时价格
                cartInfo.setSkuPrice(price);
                //数量累加----CAS
                totalNum.getAndAdd(cartInfo.getSkuNum());
                //金额累加
                totalMoney.getAndAdd(price.doubleValue() * cartInfo.getSkuNum());
                //返回
                return cartInfo;
            }).collect(Collectors.toList());
            //保存总数量
            result.put("totalNum", totalNum);
            //保存总金额
            result.put("totalMoney", totalMoney);
            //保存购物车列表
            result.put("cartInfoList", cartInfoListNew);
            //返回
            return result;
        }
        return null;
    }
    
    /**
     * 清空本次购买的购物项
     */
    @Override
    public void clearCart() {
        int delete = cartIfnoMapper.delete(
                new LambdaQueryWrapper<CartInfo>()
                        .eq(CartInfo::getUserId, CartThreadLocalUtil.get())
                        .eq(CartInfo::getIsChecked, CartConst.CART_CHECK));
        if (delete <= 0) {
            throw new RuntimeException("清除购物车失败");
        }
    }
}
