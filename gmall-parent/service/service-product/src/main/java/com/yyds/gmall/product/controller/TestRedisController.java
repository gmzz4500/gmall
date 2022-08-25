package com.yyds.gmall.product.controller;

import com.yyds.gmall.common.result.Result;
import com.yyds.gmall.product.service.TestRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: TestRedisController
 * @Author: yyd
 * @Date: 2022/8/2/002
 * @Description:测试redis
 */
@RestController
@RequestMapping(value = "/admin/product")
public class TestRedisController {

    @Autowired
    private TestRedisService testRedisService;

    /**
     * 测试写redis
     * @return
     */
    @GetMapping(value = "/test")
    public Result testRedis(){
        testRedisService.setRedisson();
        return Result.ok();
    }
}
