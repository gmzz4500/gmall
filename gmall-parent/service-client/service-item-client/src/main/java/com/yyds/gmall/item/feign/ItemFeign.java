package com.yyds.gmall.item.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * @ClassName: ItemFeign
 * @Author: yyd
 * @Date: 2022/8/3/003
 * @Description: 商品详情微服务的feign接口
 */
@FeignClient(name = "service-item", path = "/item")
public interface ItemFeign {

    /**
     *获取商品详情页的全部信息
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getItemInfo/{skuId}")
    public Map<String,Object> getItemInfo(@PathVariable(value = "skuId") Long skuId);
}
