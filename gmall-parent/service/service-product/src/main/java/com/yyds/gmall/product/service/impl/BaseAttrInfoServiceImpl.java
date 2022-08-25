package com.yyds.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yyds.gmall.model.product.BaseAttrInfo;
import com.yyds.gmall.product.mapper.BaseAttrInfoMapper;
import com.yyds.gmall.product.service.BaseAttrInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @ClassName: BaseAttrInfoServiceImpl
 * @Author: yyd
 * @Date: 2022/7/26/026
 * @Description:
 */

/***
 * 平台属性相关的接口实现类
 */
@Service
public class BaseAttrInfoServiceImpl implements BaseAttrInfoService {

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    /**
     * 主键查询
     *
     * @param id
     * @return
     */
    @Override
    public BaseAttrInfo findById(Long id) {
        return baseAttrInfoMapper.selectById(id);
    }

    /**
     * 查询所有数据
     *
     * @return
     */
    @Override
    public List<BaseAttrInfo> findAll() {
        return baseAttrInfoMapper.selectList(null);
    }

    /**
     * 添加数据
     *
     * @param baseAttrInfo
     */
    @Override
    public void addAttr(BaseAttrInfo baseAttrInfo) {
        //判断名字是否为空
        if(StringUtils.isEmpty(baseAttrInfo.getAttrName())){
            throw new RuntimeException("参数错误,名字不能为空");
        }
        //新增平台数据
        int insert = baseAttrInfoMapper.insert(baseAttrInfo);
        if (insert <= 0){
            throw new RuntimeException("新增失败,请重试!");
        }
    }

    /**
     * 修改数据
     *
     * @param baseAttrInfo
     */
    @Override
    public void updateAttr(BaseAttrInfo baseAttrInfo) {
        if (StringUtils.isEmpty(baseAttrInfo.getAttrName())){
            throw new RuntimeException("参数错误,名字不能为空");
        }
        //修改平台数据
        int update = baseAttrInfoMapper.updateById(baseAttrInfo);
        if (update < 0){
            throw new RuntimeException("修改失败,请重试");
        }
    }

    /**
     * 删除数据
     *
     * @param id
     */
    @Override
    public void deleteAttr(Long id) {
        //参数校验
        if (id == null){
            throw new RuntimeException("参数错误,id不能为空");
        }
        int i = baseAttrInfoMapper.deleteById(id);
        if (i < 0){
            throw new RuntimeException("删除失败,请重试!");
        }
    }

    /**
     * 分页条件查询
     *  @param page
     * @param size
     * @param baseAttrInfo
     * @return
     */
    @Override
    public IPage<BaseAttrInfo> findByPageConditions(Integer page,
                                                    Integer size,
                                                    BaseAttrInfo baseAttrInfo) {
        //参数校验
        if (baseAttrInfo == null){
            //没有条件的时候查询所有的数据
            return baseAttrInfoMapper.selectPage(new Page<>(page,size),null);
        }
        //拼接条件
        LambdaQueryWrapper<BaseAttrInfo> wrapper = buildQueryParam(baseAttrInfo);
        //返回结果
        return baseAttrInfoMapper.selectPage(new Page<>(page,size),wrapper);
    }

    /**
     * 分页查询
     *  @param page
     * @param size
     * @return
     */
    @Override
    public IPage findByPage(Integer page, Integer size) {
        return baseAttrInfoMapper.selectPage(new Page(page,size),null);
    }

    /**
     * 按条件查询
     *
     * @param baseAttrInfo
     * @return
     */
    @Override
    public List<BaseAttrInfo> findByConditions(BaseAttrInfo baseAttrInfo) {
        //参数校验
        if (baseAttrInfo == null){
            //没有条件的时候查询所有的数据
            return baseAttrInfoMapper.selectList(null);
        }
        //拼接条件
        LambdaQueryWrapper<BaseAttrInfo> wrapper = buildQueryParam(baseAttrInfo);
        //执行查询,返回结果
        return baseAttrInfoMapper.selectList(wrapper);
    }

    /**
     * 条件拼接
     * @param baseAttrInfo
     * @return
     */
    private LambdaQueryWrapper<BaseAttrInfo> buildQueryParam(BaseAttrInfo baseAttrInfo) {
        //条件拼接,声明条件构造器
        LambdaQueryWrapper<BaseAttrInfo> wrapper = new LambdaQueryWrapper<>();
        //条件拼接,判断id是否为空
        if (baseAttrInfo.getId() != null){
            wrapper.eq(BaseAttrInfo::getId, baseAttrInfo.getId());
        }
        //名字
        if (!StringUtils.isEmpty(baseAttrInfo.getAttrName())){
            wrapper.like(BaseAttrInfo::getAttrName,baseAttrInfo.getAttrName());
        }
        //分类id
        if (baseAttrInfo.getCategoryId() != null){
            wrapper.eq(BaseAttrInfo::getCategoryId,baseAttrInfo.getCategoryId());
        }
        //分类级别
        if (baseAttrInfo.getCategoryLevel() != null){
            wrapper.eq(BaseAttrInfo::getCategoryLevel,baseAttrInfo.getCategoryLevel());
        }
        return wrapper;
    }
}
