package com.yyds.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yyds.gmall.common.config.RedissonConfig;
import com.yyds.gmall.model.product.*;
import com.yyds.gmall.product.mapper.*;
import com.yyds.gmall.product.service.ItemService;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: ItemServiceImpl
 * @Author: yyd
 * @Date: 2022/8/1/001
 * @Description:
 */

/**
 * 供内部服务调用的接口的实现类
 */
@Service
@Log4j2
public class ItemServiceImpl implements ItemService {
    
    @Autowired
    private SkuInfoMapper skuInfoMapper;
    
    /**
     * 查询商品的详细信息
     *
     * @param skuId
     * @return
     */
    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        return skuInfoMapper.selectById(skuId);
    }
    
    @Autowired
    private RedisTemplate redisTemplate;
    
    @Autowired
    private RedissonClient redissonClient;
    
    /**
     * 从redis或数据库查询商品的信息
     *
     * @param skuId
     * @return
     */
    @Override
    public SkuInfo getSkuInfoFromDbOrRedis(Long skuId) {
        //参数校验
        if (skuId == null) {
            return null;
        }
        //查询redis中是否有这个商品的数据 key=sku:1:info
        SkuInfo skuInfo =
                (SkuInfo) redisTemplate.opsForValue().get("sku:" + skuId + ":info");
        //若redis中有商品的数据,则直接返回
        if (skuInfo != null) {
            return skuInfo;
        }
        //若redis中没有数据,加锁,保证只有一个线程查询数据库
        RLock lock = redissonClient.getLock("sku:" + skuId + ":lock");
        try {
            //加锁成功的可以操作数据库
            if (lock.tryLock(100, 100, TimeUnit.SECONDS)) {
                try {
                    //从数据库中查询到数据以后,将数据写入到redis中去
                    skuInfo = skuInfoMapper.selectById(skuId);
                    if (skuInfo == null || skuInfo.getId() == null) {
                        skuInfo = new SkuInfo();
                        //redis没有,数据库也没有
                        redisTemplate.opsForValue().set("sku:" + skuId + ":info", skuInfo, 300, TimeUnit.SECONDS);
                    } else {
                        //redis没有,数据库有
                        redisTemplate.opsForValue().set("sku:" + skuId + ":info", skuInfo, 24, TimeUnit.HOURS);
                    }
                    //返回
                    return skuInfo;
                } catch (Exception e) {
                    log.error("加锁成功,代码执行出现异常,异常内容为:" + e.getMessage());
                } finally {
                    lock.unlock();
                }
            }
        } catch (Exception e) {
            log.error("尝试加锁的时候出现异常,加锁失败,失败原因为:" + e.getMessage());
        }
        //返回结果
        return null;
    }
    
    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;
    
    /**
     * 根据category3Id查询一级二级三级分类的信息
     *
     * @param category3Id
     * @return
     */
    @Override
    public BaseCategoryView getCategory(Long category3Id) {
        //查询视图返回的数据
        return baseCategoryViewMapper.selectById(category3Id);
    }
    
    @Autowired
    private SkuImageMapper skuImageMapper;
    
    /**
     * 查询图片列表
     *
     * @param skuId
     * @return
     */
    @Override
    public List<SkuImage> getImageList(Long skuId) {
        return skuImageMapper.selectList(
                new LambdaQueryWrapper<SkuImage>()
                        .eq(SkuImage::getSkuId, skuId)
        );
    }
    
    /**
     * 查询价格列表
     *
     * @param skuId
     * @return
     */
    @Override
    public BigDecimal getPrice(Long skuId) {
        return skuInfoMapper.selectById(skuId).getPrice();
    }
    
    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;
    
    /**
     * 根据spuid和skuid查询商品的销售属性信息并且标识出当前sku的销售属性是哪几个
     *
     * @param spuId
     * @param skuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> getSpuSaleAttr(Long spuId, Long skuId) {
        return spuSaleAttrMapper.slectSpuSaleAttrBySpuIdAndSkuId(spuId, skuId);
    }
    
    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    
    /**
     * 查询键值对
     *
     * @param spuId
     * @return
     */
    @Override
    public Map getSkuIdAndValues(Long spuId) {
        //查询
        List<Map> valueList =
                skuSaleAttrValueMapper.selectSaleAttrKeyValueBySpuId(spuId);
        //返回结果优化
        Map result = new ConcurrentHashMap<>();
        //遍历处理数据
        valueList.stream().forEach((value -> {
            Object skuId = value.get("sku_id");
            Object valuesId = value.get(("values_id"));
            result.put(valuesId.toString(), skuId);
        }));
        return result;
    }
    
    @Autowired
    private BaseTradeMarkMapper baseTradeMarkMapper;
    
    /**
     * 查询品牌的信息
     *
     * @param id
     * @return
     */
    @Override
    public BaseTrademark getBaseTrademark(Long id) {
        return baseTradeMarkMapper.selectById(id);
    }
    
    @Autowired
    public BaseAttrInfoMapper baseAttrInfoMapper;
    
    /**
     * 查询指定sku的平台属性
     *
     * @param skuId
     * @return
     */
    @Override
    public List<BaseAttrInfo> getBaseAttrInfo(Long skuId) {
        return baseAttrInfoMapper.selectBaseAttrInfoBySkuId(skuId);
    }
    
    /**
     * 扣减库存的方法
     *
     * @param skuParam
     */
    @Override
    public void decountStocks(Map<String, Object> skuParam) {
        //遍历map
        skuParam.entrySet().stream().forEach(skuEntry -> {
            //获取商品的id
            String skuId = skuEntry.getKey();
            //获取商品需要扣减的库存
            Object value = skuEntry.getValue();
            //扣减库存
            int i = skuInfoMapper.decountStocks(Long.parseLong(skuId),
                    Integer.parseInt(value.toString()));
            if (i < 0) {
                throw new RuntimeException("扣减库存失败");
            }
//            //查询商品的信息
//            SkuInfo skuInfo = skuInfoMapper.selectById(Long.parseLong(skuId));
//            if (skuInfo==null||skuInfo.getId()==null) {
//                throw new RuntimeException("商品不存在,扣减库存失败");
//            }
//            //扣减库存
//            int stocks = skuInfo.getStock() - Integer.parseInt(value.toString());
//            if (stocks < 0){
//                throw new RuntimeException("库存不足,扣减库存失败");
//            }
//            //扣减成功
//            skuInfo.setStock(stocks);
//            int update = skuInfoMapper.updateById(skuInfo);
//            if (update <=  0){
//                throw new RuntimeException("扣减库存失败");
//            }
        });
    }
    
    /**
     * 回滚库存的方法
     *
     * @param skuParam
     */
    @Override
    public void rollbackStocks(Map<String, Object> skuParam) {
        //遍历map
        skuParam.entrySet().stream().forEach(skuEntry -> {
            //获取商品id
            String skuId = skuEntry.getKey();
            //获取商品需要扣减的库存
            Object value = skuEntry.getValue();
            //扣减库存
            int i = skuInfoMapper.rollbackStocks(Long.parseLong(skuId), Integer.parseInt(value.toString()));
            if (i < 0) {
                throw new RuntimeException("回滚库存失败");
            }
        });
    }
}
