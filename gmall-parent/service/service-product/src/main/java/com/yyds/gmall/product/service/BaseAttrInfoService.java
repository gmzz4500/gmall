package com.yyds.gmall.product.service;

/**
 * @ClassName: BaseAttrInfoService
 * @Author: yyd
 * @Date: 2022/7/26/026
 * @Description:
 */

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yyds.gmall.model.product.BaseAttrInfo;

import java.util.List;

/***
 * 平台属性相关的接口类
 */
public interface BaseAttrInfoService {
    /**
     * 主键查询
     * @param id
     * @return
     */
    public BaseAttrInfo findById(Long id);

    /**
     * 查询所有数据
     * @return
     */
    public List<BaseAttrInfo> findAll();

    /**
     * 添加数据
     * @param baseAttrInfo
     */
    public void addAttr(BaseAttrInfo baseAttrInfo);

    /**
     * 修改数据
     * @param baseAttrInfo
     */
    public void updateAttr(BaseAttrInfo baseAttrInfo);

    /**
     * 删除数据
     * @param id
     */
    public void deleteAttr(Long id);

    /**
     * 按条件查询
     * @param baseAttrInfo
     * @return
     */
    public List<BaseAttrInfo> findByConditions(BaseAttrInfo baseAttrInfo);

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    public IPage findByPage(Integer page, Integer size);

    /**
     * 分页条件查询
     * @param page
     * @param size
     * @param baseAttrInfo
     * @return
     */
    public IPage<BaseAttrInfo> findByPageConditions(Integer page,
                                                    Integer size,
                                                    BaseAttrInfo baseAttrInfo);
}
