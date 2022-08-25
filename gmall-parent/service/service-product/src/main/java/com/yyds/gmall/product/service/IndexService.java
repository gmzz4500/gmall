package com.yyds.gmall.product.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * @ClassName: IndexService
 * @Author: yyd
 * @Date: 2022/8/4/004
 * @Description:首页相关使用的接口类
 */

public interface IndexService {
    /**
     * 获取首页的分类信息
     *
     * @return
     */
    public List<JSONObject> getIndexCategory();
}
