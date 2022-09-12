package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseAttrValue;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * Date:2022/7/26
 * Author:cjc
 * Description:
 */
public interface BaseAttrValueService {
    /**
     * 根据id查询
     * @param id
     * @return
     */
    public BaseAttrValue findById(Long id);

    /**
     * 查询所有
     * @return
     */
    public List<BaseAttrValue> findAll();

    /**
     * 新增
     * @param baseAttrValue
     */
    public void add(BaseAttrValue baseAttrValue);

    /**
     * 修改
     * @param baseAttrValue
     */
    public void update(BaseAttrValue baseAttrValue);

    /**
     * 删除
     * @param id
     */
    public void delete(Long id);

    /**
     * 条件查询
     * @param baseAttrValue
     * @return
     */
    public List<BaseAttrValue> search(BaseAttrValue baseAttrValue);

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    public IPage<BaseAttrValue> page(Integer page, Integer size);

    /**
     * 分页带条件查询
     * @param page
     * @param size
     * @param baseAttrValue
     * @return
     */
    public IPage<BaseAttrValue> search(Integer page, Integer size, BaseAttrValue baseAttrValue);
}
