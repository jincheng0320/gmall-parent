package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.base.BaseEntity;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * Date:2022/7/26
 * Author:cjc
 * Description:
 */
@Service
public class BaseAttrValueServiceImpl implements BaseAttrValueService {
    //注入mapper
    @Resource
    private BaseAttrValueMapper baseAttrValueMapper;
    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @Override
    public BaseAttrValue findById(Long id) {
        return baseAttrValueMapper.selectById(id);
    }

    /**
     * 查询所有
     *
     * @return
     */
    @Override
    public List<BaseAttrValue> findAll() {
        return baseAttrValueMapper.selectList(null);
    }

    /**
     * 新增
     *
     * @param baseAttrValue
     */
    @Override
    public void add(BaseAttrValue baseAttrValue) {
        //校验name不能为空
        if (StringUtils.isEmpty(baseAttrValue.getValueName())){
            throw new RuntimeException("参数错误,姓名不能为空");
        }
        int insert = baseAttrValueMapper.insert(baseAttrValue);
        if (insert <= 0){
            throw new RuntimeException("新增失败,请重试");
        }
    }

    /**
     * 修改
     *
     * @param baseAttrValue
     */
    @Override
    public void update(BaseAttrValue baseAttrValue) {
        //校验name为空?
        if (StringUtils.isEmpty(baseAttrValue.getValueName())){
            throw new RuntimeException("参数错误,name不能为空");
        }
        int update = baseAttrValueMapper.updateById(baseAttrValue);
        if (update < 0){
            throw new RuntimeException("修改失败,请重试");
        }
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delete(Long id) {
        //校验id不能为空
        if (id == null){
            throw new RuntimeException("参数错误,id不能为空");
        }
        int delete = baseAttrValueMapper.deleteById(id);
        if (delete < 0){
            throw new RuntimeException("删除失败,请重试");
        }
    }

    /**
     * 条件查询
     *
     * @param baseAttrValue
     * @return
     */
    @Override
    public List<BaseAttrValue> search(BaseAttrValue baseAttrValue) {
        //条件是否为空
        if (baseAttrValue == null){
            //查所有
            return baseAttrValueMapper.selectList(null);
        }
        LambdaQueryWrapper<BaseAttrValue> wrapper = buildWrapper(baseAttrValue);
        return baseAttrValueMapper.selectList(wrapper);
    }

    /**
     * 分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<BaseAttrValue> page(Integer page, Integer size) {
        return baseAttrValueMapper.selectPage(new Page<>(page,size),null);
    }

    /**
     * 分页带条件查询
     *
     * @param page
     * @param size
     * @param baseAttrValue
     * @return
     */
    @Override
    public IPage<BaseAttrValue> search(Integer page, Integer size, BaseAttrValue baseAttrValue) {
        //条件为空?
        if (baseAttrValue == null){
            return baseAttrValueMapper.selectPage(new Page<>(page, size),null);
        }
        //条件拼接
        LambdaQueryWrapper<BaseAttrValue> wrapper = buildWrapper(baseAttrValue);
        return baseAttrValueMapper.selectPage(new Page<>(page, size),wrapper);
    }

    /**
     * 条件拼接
     * @return
     */
    private LambdaQueryWrapper<BaseAttrValue> buildWrapper(BaseAttrValue baseAttrValue) {
        //条件构造器
        LambdaQueryWrapper<BaseAttrValue> wrapper = new LambdaQueryWrapper<>();
        //id
        if (baseAttrValue.getId() != null){
            wrapper.eq(BaseEntity::getId, baseAttrValue.getId());
        }
        //属性值名称
        if (!StringUtils.isEmpty(baseAttrValue.getValueName())){
            wrapper.like(BaseAttrValue::getValueName, baseAttrValue.getValueName());
        }
        //属性id
        if (baseAttrValue.getAttrId() != null){
            wrapper.eq(BaseAttrValue::getAttrId, baseAttrValue.getAttrId());
        }
        return wrapper;
    }
}
