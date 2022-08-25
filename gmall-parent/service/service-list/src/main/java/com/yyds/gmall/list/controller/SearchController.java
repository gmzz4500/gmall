package com.yyds.gmall.list.controller;

import com.yyds.gmall.common.result.Result;
import com.yyds.gmall.list.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @ClassName: SearchController
 * @Author: yyd
 * @Date: 2022/8/6/006
 * @Description: 商品搜索的控制层
 */
@RestController
@RequestMapping(value = "/api/search")
public class SearchController {
    @Autowired
    private SearchService searchService;
    
    /**
     * 商品搜索
     * @param searchData
     * @return
     */
    @GetMapping
    public Map<String, Object> search(@RequestParam Map<String, String> searchData){
        return searchService.search(searchData);
    }
}
