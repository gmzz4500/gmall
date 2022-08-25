package com.yyds.gmall.list.service;

import java.util.Map;

/**
 * @ClassName: SearchService
 * @Author: yyd
 * @Date: 2022/8/6/006
 * @Description: 搜索相关的接口类
 */
public interface SearchService {
    /**
     * 商品搜索
     *
     * @param searchData
     * @return
     */
    public Map<String, Object> search(Map<String, String> searchData);
}
