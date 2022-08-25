package com.yyds.gmall.product.feign;

import com.alibaba.fastjson.JSONObject;
import com.yyds.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @ClassName: IndexFeign
 * @Author: yyd
 * @Date: 2022/8/4/004
 * @Description:首页使用的feign接口
 */
@FeignClient(name = "service-product",path = "/admin/product",contextId = "IndexFeign")
public interface IndexFeign {


    /**
     * 获取首页的分类信息
     * @return
     */
    @GetMapping(value = "/getIndexCategory")
    public List<JSONObject> getIndexCategory();
}
