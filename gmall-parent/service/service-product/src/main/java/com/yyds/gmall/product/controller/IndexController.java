package com.yyds.gmall.product.controller;

import com.alibaba.fastjson.JSONObject;
import com.yyds.gmall.common.cache.Java0217Cache;
import com.yyds.gmall.common.result.Result;
import com.yyds.gmall.product.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @ClassName: IndexController
 * @Author: yyd
 * @Date: 2022/8/4/004
 * @Description:前端页面使用的接口控制层
 */
@RestController
@RequestMapping(value = "/admin/product")
public class IndexController {

    @Autowired
    private IndexService indexService;

    /**
     * 获取首页的分类信息
     * @return
     */
    @GetMapping(value = "/getIndexCategory")
    @Java0217Cache(prefix = "getIndexCategory:")
    public List<JSONObject> getIndexCategory(){

        return indexService.getIndexCategory();
    }
}
