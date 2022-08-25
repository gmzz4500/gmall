package com.yyds.gmall.list.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yyds.gmall.list.service.SearchService;
import com.yyds.gmall.model.list.Goods;
import com.yyds.gmall.model.list.SearchResponseAttrVo;
import com.yyds.gmall.model.list.SearchResponseTmVo;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: SearchServiceImpl
 * @Author: yyd
 * @Date: 2022/8/6/006
 * @Description: 商品搜索相关的接口实现类
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;
    /**
     * 商品搜索
     *
     * @param searchData
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchData) {
        try {
            //拼接条件
            SearchRequest searchRequest = buildSearchParams(searchData);
            //执行查询
            SearchResponse searchResponse =
                    restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //解析结果,返回结果
            return getSearchResult(searchResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 拼接查询条件
     *
     * @param searchData
     * @return
     */
    private SearchRequest buildSearchParams(Map<String, String> searchData) {
        //拼接查询条件
        SearchRequest searchRequest = new SearchRequest("goods_java0107");
        //初始化条件构造器
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //构建组合查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //关键字不为空作为查询条件
        String keywords = searchData.get(("keywords"));
        if (!StringUtils.isEmpty(keywords)){
            boolQueryBuilder.must(QueryBuilders.matchQuery("title",keywords));
        }
        //如果品牌不为空,品牌作为查询条件
        String tradeMark = searchData.get("tradeMark");
        if (!StringUtils.isEmpty(tradeMark)) {
            String[] split = tradeMark.split(":");
            boolQueryBuilder.must(QueryBuilders.termQuery("tmId",split[0]));
        }
        //遍历所有的参数
        searchData.entrySet().stream().forEach(entry->{
            //参数的名字
            String key = entry.getKey();
            if (key.startsWith("attr_")) {
                //参数的值
                String value = entry.getValue();
                String[] split = value.split(":");
                //nested的组合条件查询构造器
                BoolQueryBuilder nestedBool = QueryBuilders.boolQuery();
                //平台属性的id必须等于用户传递的值
                nestedBool.must(QueryBuilders.termQuery("attrs.attrId",split[0]));
                //平台属性的值也要等于用户选择的值
                nestedBool.must(QueryBuilders.termQuery("attrs.attrValue",split[1]));
                //构建查询条件
                boolQueryBuilder.must(QueryBuilders.nestedQuery("attrs", nestedBool, ScoreMode.None));
            }
        });
        //价格查询条件
        String price = searchData.get("price");
        if (!StringUtils.isEmpty(price)) {
            //0-500,3000元以上
            price = price.replace("元","").replace("以上","");
            //切分
            String[] split = price.split("-");
            boolQueryBuilder.must(QueryBuilders.rangeQuery("price").gte(split[0]));
            //判断是否有第二个值
            if (split.length > 1) {
                boolQueryBuilder.must(QueryBuilders.rangeQuery("price").lt(split[1]));
            }
        }
        //设置查询条件
        builder.query(boolQueryBuilder);
        //设置品牌的聚合条件
        builder.aggregation(
                AggregationBuilders.terms("aggTmId").field("tmId")
                    .subAggregation(AggregationBuilders.terms("aggTmName").field("tmName"))
                    .subAggregation(AggregationBuilders.terms("aggTmLogoUrl").field("tmLogoUrl"))
                        .size(100)
        );
        //设置平台属性聚合条件
        builder.aggregation(
                AggregationBuilders.nested("aggAttrs","attrs")
                        .subAggregation(
                                AggregationBuilders.terms("aggAttrId").field("attrs.attrId")
                                        .subAggregation(AggregationBuilders.terms("aggAttrName").field("attrs.attrName"))
                                        .subAggregation(AggregationBuilders.terms("aggAttrValue").field("attrs.attrValue"))
                                        .size(100)
                        )
        );
        //设置排序
        String sortField = searchData.get("sortField");
        String sortRule = searchData.get("sortRule");
        if (!StringUtils.isEmpty(sortField)&&
                !StringUtils.isEmpty(sortRule)){
            //指定排序
            builder.sort(sortField,SortOrder.valueOf(sortRule));
        }else {
            //默认排序-新品
            builder.sort("id", SortOrder.DESC);
        }
        //分页实现
        String pageNum = searchData.get("pageNum");
        int page = getPage(pageNum);
        builder.from((page-1)*50);
        builder.size(50);
        //设置高亮查询
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //定义高亮的域
        highlightBuilder.field("title");
        //定义前缀html标签
        highlightBuilder.preTags("<font style=color:red>");
        //定义后缀html标签
        highlightBuilder.postTags("</font>");
        //设置高亮
        builder.highlighter(highlightBuilder);
        //指定条件
        searchRequest.source(builder);
        //返回条件
        return searchRequest;
    }
    
    /**
     * 计算页码
     * @param pageNum
     */
    private int getPage(String pageNum) {
        try {
            int i = Integer.parseInt(pageNum);
            return i>0?i:1;
        }catch (Exception e){
            return 1;
        }
    }
    
    /**
     * 解析搜索结果
     *
     * @param searchResponse
     * @return
     */
    private Map<String, Object> getSearchResult(SearchResponse searchResponse) {
        //返回结果初始化
        Map<String, Object> result = new HashMap<>();
        //获取命中的数据
        SearchHits hits = searchResponse.getHits();
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        //获取总命中的数据
        long totalHits = hits.getTotalHits();
        result.put("totalHits",totalHits);
        //商品列表初始化
        List<Goods> goodsList = new ArrayList<>();
        //遍历每条数据
        while (iterator.hasNext()){
            //获取每条数据
            SearchHit next = iterator.next();
            //获取每条文档的json字符串类型的数据
            String sourceAsString = next.getSourceAsString();
            //手动反序列化
            Goods goods = JSONObject.parseObject(sourceAsString, Goods.class);
            //获取高亮的数据
            HighlightField highlightField = next.getHighlightFields().get("title");
            if (highlightField != null){
                Text[] fragments = highlightField.getFragments();
                //防止空指针
                if (fragments != null && fragments.length > 0){
                    String title = "";
                    for (Text fragment : fragments) {
                        title += fragment;
                    }
                    //使用高亮的数据替换原始的数据
                    goods.setTitle(title);
                }
            }
            //保存数据
            goodsList.add(goods);
        }
        //保存商品列表
        result.put("goodsList",goodsList);
        //获取全部的聚合结果
        Aggregations aggregations = searchResponse.getAggregations();
        //解析品牌的聚合结果
        List<SearchResponseTmVo> searchResponseTmVoList = getTmAggResult(aggregations);
        result.put("searchResponseTmVoList",searchResponseTmVoList);
        //解析平台属性的聚合结果
        List<SearchResponseAttrVo> searchResponseAttrVoList = getAttrInfoAggResult(aggregations);
        result.put("searchResponseAttrVoList",searchResponseAttrVoList);
        //返回结果
        return result;
    }
    
    /**
     * 解析平台属性的聚合结果
     *
     * @param aggregations
     * @return
     */
    private List<SearchResponseAttrVo> getAttrInfoAggResult(Aggregations aggregations) {
        //平台属性nested聚合的结果获取
        ParsedNested aggAttrs = aggregations.get("aggAttrs");
        //获取子聚合的结果:平台属性id的聚合结果
        ParsedLongTerms aggAttrId = aggAttrs.getAggregations().get("aggAttrId");
        //遍历
        return aggAttrId.getBuckets().stream().map(attrIdBuck->{
            SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
            //获取平台属性的id
            long attrId = attrIdBuck.getKeyAsNumber().longValue();
            searchResponseAttrVo.setAttrId(attrId);
            //获取子聚合平台--平台属性的名字
            ParsedStringTerms aggAttrName = attrIdBuck.getAggregations().get("aggAttrName");
            if (!aggAttrName.getBuckets().isEmpty()) {
               //获取名字--只需要一个即可
                String attrName = aggAttrName.getBuckets().get(0).getKeyAsString();
                searchResponseAttrVo.setAttrName(attrName);
            }
            //获取子聚合平台--平台属性的值
            ParsedStringTerms aggAttrValue = attrIdBuck.getAggregations().get("aggAttrValue");
            //把这个平台属性的所有值都取出来
            List<String> attrValueList = aggAttrValue.getBuckets().stream().map(attrValueBuck -> {
                //获取每个值的名字
                return attrValueBuck.getKeyAsString();
            }).collect(Collectors.toList());
            searchResponseAttrVo.setAttrValueList(attrValueList);
            //返回
            return searchResponseAttrVo;
        }).collect(Collectors.toList());
    }
    
    /**
     * 获取品牌的聚合结果
     *
     * @param aggregations
     * @return
     */
    private List<SearchResponseTmVo> getTmAggResult(Aggregations aggregations) {
        //获取品牌id的聚合结果
        ParsedLongTerms aggTmId = aggregations.get("aggTmId");
        //遍历
        return aggTmId.getBuckets().stream().map(tmIdBuck ->{
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
            //获取到聚合后的每个品牌的id
            long tmId = ((Terms.Bucket) tmIdBuck).getKeyAsNumber().longValue();
            searchResponseTmVo.setTmId(tmId);
            //获取子聚合的结果-品牌名字
            ParsedStringTerms aggTmName =
                    ((Terms.Bucket) tmIdBuck).getAggregations().get("aggTmName");
            //获取一个品牌的名字
            if(!aggTmName.getBuckets().isEmpty()){
                //获取品牌的名字
                String tmName = aggTmName.getBuckets().get(0).getKeyAsString();
                searchResponseTmVo.setTmName(tmName);
            }
            //获取子聚合的结果-logo地址
            ParsedStringTerms aggTmLogoUrl =
                    ((Terms.Bucket) tmIdBuck).getAggregations().get("aggTmLogoUrl");
            if(!aggTmLogoUrl.getBuckets().isEmpty()){
                //获取品牌的logourl
                String tmLogoUrl = aggTmLogoUrl.getBuckets().get(0).getKeyAsString();
                searchResponseTmVo.setTmLogoUrl(tmLogoUrl);
            }
            //返回
            return searchResponseTmVo;
        }).collect(Collectors.toList());
    }
}
