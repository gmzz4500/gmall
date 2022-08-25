package com.yyds.gmall.list.controller;

import com.yyds.gmall.common.result.Result;
import com.yyds.gmall.list.service.GoodsService;
import com.yyds.gmall.model.list.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.ResultSet;

/**
 * @ClassName: ListController
 * @Author: yyd
 * @Date: 2022/8/5/005
 * @Description:创建商品搜索相关的索引和映射
 */
@RestController
@RequestMapping(value = "/api/list")
public class ListController {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    /**
     * 创建索引和映射
     * @return
     */
    @GetMapping(value = "/create")
    public Result createIndexAndMapping(){
        //创建索引
        elasticsearchRestTemplate.createIndex(Goods.class);
        //创建映射
        elasticsearchRestTemplate.putMapping(Goods.class);
        return Result.ok();
    }


    @Autowired
    private GoodsService goodsService;

    /**
     * 新增商品到es
     * @param skuId
     * @return
     */
    @GetMapping(value = "/add/{skuId}")
    public Boolean addGoods(@PathVariable(value = "skuId") Long skuId){
        goodsService.addGoodsToEs(skuId);
        return true;
    }
    /**
     * 删除商品
     * @param goodsId
     * @return
     */
    @GetMapping(value = "/delete/{goodsId}")
    public Boolean deletGoods(@PathVariable(value = "goodsId") Long goodsId){
        goodsService.removeGoodsFromEs(goodsId);
        return true;
    }

    /**
     * 新增热度值
     * @param goodsId
     * @return
     */
    @GetMapping(value = "/addHotScore/{goodsId}")
    public Result addHotScore(@PathVariable(value = "goodsId") Long goodsId){
        goodsService.addHotScore(goodsId);
        return Result.ok();
    }
}
