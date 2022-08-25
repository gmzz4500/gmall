package com.yyds.gmall.list.dao;

import com.yyds.gmall.model.list.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @ClassName: GoodsDao
 * @Author: yyd
 * @Date: 2022/8/5/005
 * @Description: es中商品相关的dao接口
 */
@Repository
public interface GoodsDao extends ElasticsearchRepository<Goods,Long> {
}
