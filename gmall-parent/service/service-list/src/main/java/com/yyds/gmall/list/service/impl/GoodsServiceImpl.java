package com.yyds.gmall.list.service.impl;

import com.yyds.gmall.list.dao.GoodsDao;
import com.yyds.gmall.list.service.GoodsService;
import com.yyds.gmall.model.list.Goods;
import com.yyds.gmall.model.list.SearchAttr;
import com.yyds.gmall.model.product.BaseAttrInfo;
import com.yyds.gmall.model.product.BaseCategoryView;
import com.yyds.gmall.model.product.BaseTrademark;
import com.yyds.gmall.model.product.SkuInfo;
import com.yyds.gmall.product.feign.ItemFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
  * @ClassName: GoodsServiceImpl
  * @Author: yyd
  * @Date: 2022/8/5/005
  * @Description:
*/
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private ItemFeign itemFeign;
    /**
     * 将数据库中上架的商品写入到es
     *
     * @param skuId
     */
    @Override
    public void addGoodsToEs(Long skuId) {
        //商品对象初始化
        Goods goods = new Goods();
        //补全商品的属性
        SkuInfo skuInfo = itemFeign.getSkuInfo(skuId);
        if (null == skuInfo || null == skuInfo.getId()){
            throw new RuntimeException("商品不存在");
        }
        //补全商品的id
        goods.setId(skuInfo.getId());
        //补全默认图片
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        //补全商品的价格
        BigDecimal price = itemFeign.getPrice(skuId);
        goods.setPrice(price.doubleValue());
        //创建时间
        goods.setCreateTime(new Date());
        //品牌
        BaseTrademark trademark = itemFeign.getBaseTrademark(skuInfo.getTmId());
        goods.setTmId(trademark.getId());
        goods.setTmName(trademark.getTmName());
        goods.setTmLogoUrl(trademark.getLogoUrl());
        //保存分类信息
        Long category3Id = skuInfo.getCategory3Id();
        BaseCategoryView category = itemFeign.getCategory(category3Id);
        goods.setCategory1Id(category.getCategory1Id());
        goods.setCategory1Name(category.getCategory1Name());
        goods.setCategory2Id(category.getCategory2Id());
        goods.setCategory2Name(category.getCategory2Name());
        goods.setCategory3Id(category.getCategory3Id());
        goods.setCategory3Name(category.getCategory3Name());
        //平台属性
        List<BaseAttrInfo> baseAttrInfoList = itemFeign.getBaseAttrInfo(skuId);
        List<SearchAttr> attrs = baseAttrInfoList.stream().map(baseAttrInfo -> {
            //结果初始化
            SearchAttr searchAttr = new SearchAttr();
            searchAttr.setAttrId(baseAttrInfo.getId());
            searchAttr.setAttrName(baseAttrInfo.getAttrName());
            searchAttr.setAttrValue(baseAttrInfo.getAttrValueList().get(0).getValueName());
            //返回
            return searchAttr;
        }).collect(Collectors.toList());
        goods.setAttrs(attrs);
        //保存商品的数据
        goodsDao.save(goods);
    }

    /**
     * 移除商品
     *
     * @param goodsId
     */
    @Override
    public void removeGoodsFromEs(Long goodsId) {
        //删除商品
        goodsDao.deleteById(goodsId);
    }

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 为商品加热度值
     *
     * @param goodsId
     */
    @Override
    public void addHotScore(Long goodsId) {
        //参数校验
        if (null == goodsId){
            return;
        }
        //将商品的热度值存储到redis
        Double score = redisTemplate.opsForZSet().incrementScore("goods_hot_score", goodsId, 1);
        //每当热度值的数值可以对10整除的时候,更新一次se
        if (score.intValue() % 10 ==0) {
            //查询商品在es中是否存在
            Optional<Goods> optionalGoods = goodsDao.findById(goodsId);
            if (optionalGoods.isPresent()) {
                //获取商品
                Goods goods = optionalGoods.get();
                //存在获取商品的热度值,+1
                goods.setHotScore(score.longValue());
                //更新商品的数据到es中去
                goodsDao.save(goods);
            }
        }

    }
}
