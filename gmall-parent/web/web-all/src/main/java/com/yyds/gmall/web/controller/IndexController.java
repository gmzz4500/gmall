package com.yyds.gmall.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.yyds.gmall.product.feign.IndexFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @ClassName: IndexController
 * @Author: yyd
 * @Date: 2022/8/4/004
 * @Description:首页页面的控制层
 */
@Controller
@RequestMapping(value = "/index")
public class IndexController {

    @Autowired
    private IndexFeign indexFeign;
    /**
     * 打开首页
     * @return
     */
    @GetMapping
    public String index(Model model){
        //远程调用商品管理微服务,查询分类的信息
        List<JSONObject> categoryList = indexFeign.getIndexCategory();
        //数据存储到model
        model.addAttribute("categoryList",categoryList);
        return "index1";
    }
}
