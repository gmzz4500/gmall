package com.yyds.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yyds.gmall.common.constant.ProductConst;
import com.yyds.gmall.list.feign.ListFeign;
import com.yyds.gmall.model.product.*;
import com.yyds.gmall.product.mapper.*;
import com.yyds.gmall.product.service.ManageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @ClassName: ManageServiceImpl
 * @Author: yyd
 * @Date: 2022/7/27/027
 * @Description:
 */

/***
 * 后台管理接口的实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Log4j2
public class ManageServiceImpl implements ManageService {
    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;
    /**
     * 查询所有的一级分类
     *
     * @return
     */
    @Override
    public List<BaseCategory1> getCategory1() {
        return baseCategory1Mapper.selectList(null);
    }

    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;
    /**
     * 根据一级分类id查询所有的二级分类
     *
     * @param c1Id
     * @return
     */
    @Override
    public List<BaseCategory2> getCategory2(Long c1Id) {
        return baseCategory2Mapper.selectList(
                new LambdaQueryWrapper<BaseCategory2>()
                        .eq(BaseCategory2::getCategory1Id,c1Id));
    }

    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;
    /**
     * 根据二级分类id查询所有的三级分类
     *
     * @param c2Id
     * @return
     */
    @Override
    public List<BaseCategory3> getCategory3(Long c2Id) {
        return baseCategory3Mapper.selectList(
                new LambdaQueryWrapper<BaseCategory3>()
                        .eq(BaseCategory3::getCategory2Id,c2Id));
    }

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;
    /**
     * 保存平台属性
     *
     * @param baseAttrInfo
     */
    @Override
    public void saveBaseAttrInfo(BaseAttrInfo baseAttrInfo) {
        //参数校验
        if (baseAttrInfo == null ||
                StringUtils.isEmpty(baseAttrInfo.getAttrName())){
            throw new RuntimeException("参数错误");
        }
        //判断用户的行为到底是修改还是新增
        if (baseAttrInfo.getId() != null){
            //修改
            int update = baseAttrInfoMapper.updateById(baseAttrInfo);
            if (update < 0){
                throw new RuntimeException("修改平台属性名称失败!!");
            }
            //旧的平台属性的值全部删除
            int delete = baseAttrValueMapper.delete(
                    new LambdaQueryWrapper<BaseAttrValue>()
                            .eq(BaseAttrValue::getAttrId, baseAttrInfo.getId()));
            if (delete < 0){
                throw new RuntimeException("修改平台属性名称失败!");
            }
        }else {
            //保存平台属性名称表
            int insert = baseAttrInfoMapper.insert(baseAttrInfo);
            if (insert <= 0){
                throw new RuntimeException("新增平台属性名称失败");
            }
        }
        //保存成功,则平台属性的id值就有了
        Long attrId = baseAttrInfo.getId();
        //补全到每个平台属性值中去,同时新增平台属性值
        baseAttrInfo.getAttrValueList().stream().forEach(baseAttrValue -> {
            //补全平台属性的id
            baseAttrValue.setAttrId(attrId);
            //保存
            int insert1 = baseAttrValueMapper.insert(baseAttrValue);
            if (insert1 <= 0){
                throw new RuntimeException("新增平台属性值失败!");
            }
        });
    }

    /**
     * 根据分类的id查询分类的平台属性列表
     *
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @Override
    public List<BaseAttrInfo> getBaseAttrInfo(Long category1Id, Long category2Id, Long category3Id) {
        return baseAttrInfoMapper.selectBaseAttrInfoByCategoryId(category1Id,category2Id,category3Id);
    }

    /**
     * 根据平台属性id查询平台属性值列表
     *
     * @param attrId
     * @return
     */
    @Override
    public List<BaseAttrValue> getBaseAttrValue(Long attrId) {
        return baseAttrValueMapper.selectList(new LambdaQueryWrapper<BaseAttrValue>().eq(BaseAttrValue::getAttrId,attrId));
    }

    @Autowired
    private BaseTradeMarkMapper baseTradeMarkMapper;
    /**
     * 查询所有的品牌列表
     *
     * @return
     */
    @Override
    public List<BaseTrademark> getBaseTrademark() {
        return baseTradeMarkMapper.selectList(null);
    }

    /**
     * 查询单个品牌
     *
     * @param tmId
     * @return
     */
    @Override
    public BaseTrademark getTrademark(Long tmId) {
        return baseTradeMarkMapper.selectById(tmId);
    }

    /**
     * 分页查询品牌列表
     *  @param page
     * @param size
     * @return
     */
    @Override
    public IPage<BaseTrademark> baseTrademark(Integer page, Integer size) {
        return baseTradeMarkMapper.selectPage(new Page<>(page,size),null);
    }

    /**
     * 添加品牌
     * @param baseTrademark
     */
    @Override
    public void saveBaseTrademark(BaseTrademark baseTrademark) {
        //参数校验
        if (baseTrademark.getTmName() == null || baseTrademark.getLogoUrl()==null){
            throw new RuntimeException("参数异常");
        }
        //新增
        int insert = baseTradeMarkMapper.insert(baseTrademark);
        if (insert <= 0){
            throw new RuntimeException("新增失败");
        }

        }

    /**
     * 修改品牌
     * @param baseTrademark
     */
    @Override
    public void updateBaseTrademark(BaseTrademark baseTrademark) {
        //参数校验
        if (baseTrademark.getTmName() == null || baseTrademark.getLogoUrl()==null){
            throw new RuntimeException("参数异常");
        }
        //修改
        int update = baseTradeMarkMapper.updateById(baseTrademark);
        if (update < 0) {
            throw new RuntimeException("修改失败");
        }
        //删除品牌名称
        baseTradeMarkMapper.delete(
                new LambdaQueryWrapper<BaseTrademark>()
                        .eq(BaseTrademark::getTmName,baseTrademark.getTmName()));
        //删除品牌logo
        baseTradeMarkMapper.delete(
                new LambdaQueryWrapper<BaseTrademark>()
                        .eq(BaseTrademark::getLogoUrl,baseTrademark.getLogoUrl()));
        int insert = baseTradeMarkMapper.insert(baseTrademark);
        if (insert <= 0){
            throw new RuntimeException("操作失败");
        }
    }

    /**
     * 删除品牌
     *
     * @param tmId
     */
    @Override
    public void deleteBaseTrademark(Long tmId) {
        baseTradeMarkMapper.deleteById(tmId);
    }

    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;
    /**
     * 查询所有的基础属性列表
     *
     * @return
     */
    @Override
    public List<BaseSaleAttr> getBaseSaleAttr() {
        return baseSaleAttrMapper.selectList(null);
    }

    @Autowired
    private SpuInfoMapper spuInfoMapper;
    /**
     * 保存Spu的信息和修改spu的信息
     *
     * @param spuInfo
     */
    @Override
    public void saveOrUpdateSpuInfo(SpuInfo spuInfo) {
        //参数校验
        if (spuInfo == null){
            throw new RuntimeException("参数错误");
        }
        //判断spu的id是否为空
        if (spuInfo.getId() == null){
            //为空,新增
            int insert = spuInfoMapper.insert(spuInfo);
            if (insert <= 0){
                throw new RuntimeException("新增失败");
            }
        }else {
            //不为空,修改
            int updateById = spuInfoMapper.updateById(spuInfo);
            if (updateById < 0){
                throw new RuntimeException("修改失败");
            }
            //删除图片表
            int delete1 = spuImageMapper.delete(
                    new LambdaQueryWrapper<SpuImage>().
                            eq(SpuImage::getSpuId, spuInfo.getId()));
            //删除销售属性名称表
            int delete2 = spuSaleAttrMapper.delete(
                    new LambdaQueryWrapper<SpuSaleAttr>().
                            eq(SpuSaleAttr::getSpuId, spuInfo.getId()));
            //删除销售属性值表
            int delete3 = spuSaleAttrValueMapper.delete(
                    new LambdaQueryWrapper<SpuSaleAttrValue>().
                            eq(SpuSaleAttrValue::getSpuId, spuInfo.getId()));
            if (delete1 < 0 || delete2 < 0 || delete3 < 0){
                throw new RuntimeException("修改失败");
            }
        }
        //获取spu的id
        Long spuId = spuInfo.getId();
        //新增图片表
        saveSpuImageInfo(spuInfo.getSpuImageList(),spuId);
        //新增销售属性名称表
        saveSpuSaleAttr(spuInfo.getSpuSaleAttrList(),spuId);
    }


    /**
     * 分页条件查询
     *  @param page
     * @param size
     * @param category3Id
     * @return
     */
    @Override
    public IPage<SpuInfo> pageSpuInfo(Integer page, Integer size, Long category3Id) {
        return spuInfoMapper.selectPage(
                new Page<>(page,size),
                new LambdaQueryWrapper<SpuInfo>().
                        eq(SpuInfo::getCategory3Id,category3Id));
    }

    /**
     * 根据spu的id查询spu的销售属性名称和销售属性值的列表
     * @param spuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> getSpuSaleAttr(Long spuId) {
        return spuSaleAttrMapper.selectSpuSaleAttrBySpuId(spuId);
    }

    /**
     * 根据spu的id查询图片列表
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SpuImage> getSpuImage(Long spuId) {
        return spuImageMapper.selectList(new LambdaQueryWrapper<SpuImage>().eq(SpuImage::getSpuId,spuId));
    }

    @Autowired
    private SkuInfoMapper skuInfoMapper;
    /**
     * 新增或者修改sku的信息
     *
     * @param skuInfo
     */
    @Override
    public void SaveOrUpdateSkuInfo(SkuInfo skuInfo) {
        //参数校验
        if (skuInfo == null){
            throw new RuntimeException("参数错误");
        }
        //判断sku的id是否为空
        if (skuInfo.getId() == null){
            //为空,新增
            int insert = skuInfoMapper.insert(skuInfo);
            if (insert <= 0){
                throw new RuntimeException("新增sku失败");
            }
        }else {
            //不为空,修改
            int updateById = skuInfoMapper.updateById(skuInfo);
            if (updateById < 0){
                throw new RuntimeException("修改sku失败");
            }
            //删除图片表
            int delete1 = skuImageMapper.delete(
                    new LambdaQueryWrapper<SkuImage>()
                            .eq(SkuImage::getSkuId, skuInfo.getId()));
            //删除sku销售属性表
            int delete2 = skuSaleAttrValueMapper.delete(
                    new LambdaQueryWrapper<SkuSaleAttrValue>()
                            .eq(SkuSaleAttrValue::getSkuId, skuInfo.getId()));
            //删除sku销售属性值表
            int delete3 = skuAttrValueMapper.delete(
                    new LambdaQueryWrapper<SkuAttrValue>()
                            .eq(SkuAttrValue::getSkuId, skuInfo.getId()));
            if (delete1 < 0 || delete2 < 0 || delete3 < 0){
                throw new RuntimeException("修改失败");
            }
        }
        //获取sku的id
        Long skuId = skuInfo.getId();
        //新增图片表
        saveSkuImage(skuInfo.getSkuImageList(),skuId);
        //新增sku的销售属性
        saveSkuSaleAttrValue(skuInfo.getSkuSaleAttrValueList(),skuInfo.getId(),skuInfo.getSpuId());
        //新增sku的平台属性
        saveSkuAttrValue(skuInfo.getSkuAttrValueList(),skuId);
    }

    /**
     * 分页查询sku的信息
     *  @param page
     * @param size
     * @return
     */
    @Override
    public IPage<SkuInfo> pageSkuInfo(Integer page, Integer size) {
        return skuInfoMapper.selectPage(new Page<>(page,size),null);
    }

    @Autowired
    private ListFeign listFeign;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    /**
     * 上架或下架
     *
     * @param skuId
     * @param status
     */
    @Override
    public void upOrDown(Long skuId, Short status) {
        //参数校验
        if (skuId == null){
            throw new RuntimeException("参数错误");
        }
        //查询商品的数据
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if (skuInfo == null || skuInfo.getId() == null){
            throw new RuntimeException("该商品不存在");
        }
        //商品存在则修改,否则结束
        skuInfo.setIsSale(status);
        int updateById = skuInfoMapper.updateById(skuInfo);
        if (updateById < 0){
            throw new RuntimeException("操作失败");
        }
        //设置可靠性传递
        rabbitTemplate.setReturnCallback((a,b,c,d,e)->{
//            重发模拟
//            rabbitTemplate.convertAndSend(d, e, skuId + "");
            log.error("上下架消息发送失败,消息没有抵达队列,商品的id为:" + new String(a.getBody()));
            log.error("上下架消息发送失败,错误码为:" + b);
            log.error("上下架消息发送失败,错误的内容为:" + c);
            log.error("上下架消息发送失败,同步消息的交换机为:" + d);
            log.error("上下架消息发送失败,消息的routingkey为:" + e);
        });
        //需要和es进行数据同步------同步调用 待优化为异步--TODO
        if (ProductConst.SKU_ON_SALE.equals(status)){
//            listFeign.addGoods(skuId);
            rabbitTemplate.convertAndSend("sku_exchange", "sku.upper", skuId + "");
        }else {
//            listFeign.deletGoods(skuId);
            rabbitTemplate.convertAndSend("sku_exchange", "sku.down", skuId + "");
        }
    }

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;
    /**
     * 新增sku的平台属性
     * @param skuAttrValueList
     * @param skuId
     */
    private void saveSkuAttrValue(List<SkuAttrValue> skuAttrValueList, Long skuId) {
        //遍历新增
        skuAttrValueList.stream().forEach(skuAttrValue -> {
            //补全skuId
            skuAttrValue.setSkuId(skuId);
            //保存
            int insert = skuAttrValueMapper.insert(skuAttrValue);
            if (insert <= 0){
                throw new RuntimeException("新增sku的平台属性失败");
            }
        });
    }

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    /**
     * 新增sku的销售属性
     * @param skuSaleAttrValueList
     * @param skuId
     * @param spuId
     */
    private void saveSkuSaleAttrValue(List<SkuSaleAttrValue> skuSaleAttrValueList, Long skuId, Long spuId) {
        //遍历新增
        skuSaleAttrValueList.stream().forEach(skuSaleAttrValue -> {
            //补全skuId
            skuSaleAttrValue.setSkuId(skuId);
            //补全spuId
            skuSaleAttrValue.setSpuId(spuId);
            //保存
            int insert = skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            if (insert <= 0){
                throw new RuntimeException("新增sku销售属性失败");
            }
        });
    }

    @Autowired
    private SkuImageMapper skuImageMapper;
    /**
     * 新增sku图片的私有方法
     * @param skuImageList
     * @param skuId
     */
    private void saveSkuImage(List<SkuImage> skuImageList, Long skuId) {
        //遍历新增
        skuImageList.stream().forEach(skuImage -> {
            //补全skuId
            skuImage.setSkuId(skuId);
            //保存
            int insert = skuImageMapper.insert(skuImage);
            if (insert <= 0){
                throw new RuntimeException("新增sku图片失败");
            }
        });
    }

    @Autowired
    private SpuImageMapper spuImageMapper;
    /**
     * 保存spu的图片
     * @param spuImageList
     * @param spuId
     */
    private void saveSpuImageInfo(List<SpuImage> spuImageList, Long spuId) {
        //遍历新增
        spuImageList.stream().forEach(spuImage -> {
            //补全spu的id
            spuImage.setSpuId(spuId);
            //新增
            int insert = spuImageMapper.insert(spuImage);
            if (insert <= 0){
                throw new RuntimeException("spu的图片新增失败");
            }
        });
    }

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;
    /**
     * 保存spu的销售属性名称
     * @param spuSaleAttrList
     * @param spuId
     */
    private void saveSpuSaleAttr(List<SpuSaleAttr> spuSaleAttrList,
                                 Long spuId) {
        //遍历新增
        spuSaleAttrList.stream().forEach(spuSaleAttr -> {
            //补全spu的id
            spuSaleAttr.setSpuId(spuId);
            //新增
            int insert = spuSaleAttrMapper.insert(spuSaleAttr);
            if (insert <= 0){
                throw new RuntimeException("新增spu的销售属性名称失败");
            }
            //新增销售属性值表
            saveSpuSaleAttrValue(spuSaleAttr.getSpuSaleAttrValueList(),spuId,spuSaleAttr.getSaleAttrName());
        });
    }

    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    /**
     * 保存spu的销售属性值对象
     * @param spuSaleAttrValueList
     * @param spuId
     */
    private void saveSpuSaleAttrValue(List<SpuSaleAttrValue> spuSaleAttrValueList,
                                      Long spuId,
                                      String saleAttrName) {
        //遍历新增
        spuSaleAttrValueList.stream().forEach(spuSaleAttrValue -> {
            //补全spu的id
            spuSaleAttrValue.setSpuId(spuId);
            //补全销售属性的名字
            spuSaleAttrValue.setSaleAttrName(saleAttrName);
            //新增
            int insert = spuSaleAttrValueMapper.insert(spuSaleAttrValue);
            if (insert <= 0){
                throw new RuntimeException("新增spu的销售属性值对象失败");
            }
        });
    }
}
