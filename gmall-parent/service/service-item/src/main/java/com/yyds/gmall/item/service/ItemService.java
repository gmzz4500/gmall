package com.yyds.gmall.item.service;

/**
 * @ClassName: ItemService
 * @Author: yyd
 * @Date: 2022/8/1/001
 * @Description:
 */

import java.util.Map;

/**
 * 商品详情页使用的接口类
 */
public interface ItemService {
    /**
     * 获取商品详情页所需要的全部数据
     *
     * @param skuId
     * @return
     */
    public Map<String, Object> getItemInfo(Long skuId);

}
