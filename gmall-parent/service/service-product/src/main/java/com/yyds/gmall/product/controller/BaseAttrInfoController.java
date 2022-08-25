package com.yyds.gmall.product.controller;

/**
 * @ClassName: BaseAttrInfoController
 * @Author: yyd
 * @Date: 2022/7/26/026
 * @Description:
 */

import com.yyds.gmall.common.result.Result;
import com.yyds.gmall.model.product.BaseAttrInfo;
import com.yyds.gmall.product.service.BaseAttrInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/***
 * 平台属性相关的控制层
 */
@RestController
@RequestMapping(value = "/api/baseAttrInfo")
public class BaseAttrInfoController {

    @Autowired
    private BaseAttrInfoService baseAttrInfoService;

    /**
     * 主键查询
     * GET参数传递:
     * 1.http://localhost:8206/api/baseAttrInfo/findById?id=1
     * 2.http://localhost:8206/api/baseAttrInfo/findById/1
     * @param id
     * @return
     */
    @GetMapping(value = "/findById")
    public Result findById(Long id){
        return Result.ok(baseAttrInfoService.findById(id));
    }

    /**
     * 查询所有数据
     * @return
     */
    @GetMapping(value = "/findAll")
    public Result findAll(){
        return Result.ok(baseAttrInfoService.findAll());
    }

    /**
     * 新增平台数据
     * @param baseAttrInfo
     * @return
     */
    @PostMapping
    public Result addAttr(@RequestBody BaseAttrInfo baseAttrInfo){
        baseAttrInfoService.addAttr(baseAttrInfo);
        return Result.ok();
    }

    /**
     * 修改
     * @param baseAttrInfo
     * @return
     */
    @PutMapping
    public Result updateAttr(@RequestBody BaseAttrInfo baseAttrInfo){
        baseAttrInfoService.updateAttr(baseAttrInfo);
        return Result.ok();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}")
    public Result deleteAttr(@PathVariable(value = "id") Long id){
        baseAttrInfoService.deleteAttr(id);
        return Result.ok();
    }

    /**
     * 按条件查询
     * @param baseAttrInfo
     * @return
     */
    @PostMapping(value = "/findByConditions")
    public Result findByConditions(@RequestBody BaseAttrInfo baseAttrInfo){
        return Result.ok(baseAttrInfoService.findByConditions(baseAttrInfo));
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @GetMapping(value = "/findByPage/{page}/{size}")
    public Result findByPage(@PathVariable(value = "page") Integer page,
                             @PathVariable(value = "size") Integer size){
        return Result.ok(baseAttrInfoService.findByPage(page,size));
    }

    @PostMapping(value = "/findByPageConditions/{page}/{size}")
    public Result findByPageConditions(@PathVariable(value = "page") Integer page,
                                       @PathVariable(value = "size") Integer size,
                                       @RequestBody BaseAttrInfo baseAttrInfo){
        return Result.ok(baseAttrInfoService.findByPageConditions(page,size,baseAttrInfo));
    }
}
