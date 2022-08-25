package com.yyds.gmall.list.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @ClassName: ListFeign
 * @Author: yyd
 * @Date: 2022/8/5/005
 * @Description:搜索微服务数据同步的feign接口
 */
@FeignClient(name = "service-list",path ="/api/list",contextId = "ListFeign")
public interface ListFeign {
    /**
     * 新增商品到es
     * @param skuId
     * @return
     */
    @GetMapping(value = "/add/{skuId}")
    public Boolean addGoods(@PathVariable(value = "skuId") Long skuId);
    /**
     * 删除商品
     * @param goodsId
     * @return
     */
    @GetMapping(value = "/delete/{goodsId}")
    public Boolean deletGoods(@PathVariable(value = "goodsId") Long goodsId);
}
