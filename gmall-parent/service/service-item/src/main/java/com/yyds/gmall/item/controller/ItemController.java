package com.yyds.gmall.item.controller;

import com.yyds.gmall.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @ClassName: ItemController
 * @Author: yyd
 * @Date: 2022/8/1/001
 * @Description:
 */

/***
 * 用户查询商品详情的接口
 */
@RestController
@RequestMapping(value = "/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    /**
     *获取商品详情页的全部信息
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getItemInfo/{skuId}")
    public Map<String, Object> getItemInfo(@PathVariable(value = "skuId") Long skuId) {
        return itemService.getItemInfo(skuId);
    }
}
