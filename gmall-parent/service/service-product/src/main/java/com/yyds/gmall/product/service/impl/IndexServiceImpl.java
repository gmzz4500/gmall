package com.yyds.gmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yyds.gmall.model.product.BaseCategoryView;
import com.yyds.gmall.product.mapper.BaseCategoryViewMapper;
import com.yyds.gmall.product.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: IndexServiceImpl
 * @Author: yyd
 * @Date: 2022/8/4/004
 * @Description:首页相关使用的接口实现类
 */
@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;
    /**
     * 获取首页的分类信息
     *
     * @return
     */
    @Override
    public List<JSONObject> getIndexCategory() {
        //查询所有分类的信息:包含一级二级三级分类
        List<BaseCategoryView> baseCategoryViewList1 =
                baseCategoryViewMapper.selectList(null);
        //需要以一级分类为单位进行分桶/分组
        Map<Long, List<BaseCategoryView>> category1Map =
                baseCategoryViewList1.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        return category1Map.entrySet().stream().map(category1Entry -> {
            //返回结果初始化
            JSONObject category1Json = new JSONObject();
            //获取每个一级分类的id
            Long category1Id = category1Entry.getKey();
            category1Json.put("categoryId", category1Id);
            //每个一级分类对应的二级分类的列表
            List<BaseCategoryView> baseCategoryViewList2 = category1Entry.getValue();
            //获取每个一级分类的name
            String category1Name = baseCategoryViewList2.get(0).getCategory1Name();
            category1Json.put("categoryName", category1Name);
            //再以二级分类为单位进行分桶/分组,获取二级分类和三级分类的一对多关系
            Map<Long, List<BaseCategoryView>> category2Map =
                    baseCategoryViewList2.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            List<JSONObject> category2JsonList = category2Map.entrySet().stream().map(category2Entry -> {
                //返回结果初始化
                JSONObject category2Json = new JSONObject();
                //获取每个二级分类的id
                Long category2Id = category2Entry.getKey();
                category2Json.put("categoryId", category2Id);
                //每个二级分类对应的三级分类的列表----三级分类不重复
                List<BaseCategoryView> baseCategoryViewList3 = category2Entry.getValue();
                //获取二级分类的名字
                String category2Name = baseCategoryViewList3.get(0).getCategory2Name();
                category2Json.put("categoryName", category2Name);
                //获取每个三级分类的id和name
                List<JSONObject> category3JsonList = baseCategoryViewList3.stream().map(baseCategoryView -> {
                    //返回结果初始化
                    JSONObject category3Json = new JSONObject();
                    //获取三级分类的id
                    Long category3Id = baseCategoryView.getId();
                    category3Json.put("categoryId", category3Id);
                    //获取三级分类的name
                    String category3Name = baseCategoryView.getCategory3Name();
                    category3Json.put("categoryName", category3Name);
                    return category3Json;
                }).collect(Collectors.toList());
                //保存这个二级分类和三级分类之间的一对多关系
                category2Json.put("childCategory", category3JsonList);
                //返回
                return category2Json;
            }).collect(Collectors.toList());
            //保存这个一级分类和二级分类之间的一对多关系
            category1Json.put("childCategory", category2JsonList);
            //返回
            return category1Json;
        }).collect(Collectors.toList());
    }
}
