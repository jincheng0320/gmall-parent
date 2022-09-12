package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * Date:2022/7/26
 * Author:cjc
 * Description:平台属性的相关接口
 */
public interface BaseAttrInfoService {

    /**
     * 根据id查询
     * @param id
     * @return
     */
    public BaseAttrInfo findById(Long id);

    /**
     * 查询所有
     * @return
     */
    public List<BaseAttrInfo> findALL();

    /**
     * 新增
     * @param baseAttrInfo
     */
    public void add(BaseAttrInfo baseAttrInfo);

    /**
     * 修改
     * @param baseAttrInfo
     */
    public void update(BaseAttrInfo baseAttrInfo);

    /**
     * 删除
     * @param id
     */
    public void delete(Long id);

    /**
     * 条件查询
     * @param baseAttrInfo
     * @return
     */
    public List<BaseAttrInfo> search(BaseAttrInfo baseAttrInfo);

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    public IPage<BaseAttrInfo> page(Integer page, Integer size);

    /**
     * 条件分页查询
     * @param page
     * @param size
     * @param baseAttrInfo
     * @return
     */
    public IPage<BaseAttrInfo> search(Integer page, Integer size, BaseAttrInfo baseAttrInfo);
}
