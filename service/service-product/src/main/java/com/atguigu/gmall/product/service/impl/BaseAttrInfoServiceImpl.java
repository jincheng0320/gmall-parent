package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.base.BaseEntity;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
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
 * Description:平台属性相关接口实现类
 */
@Service
public class BaseAttrInfoServiceImpl implements BaseAttrInfoService {
    //mapper注入
    @Resource
    private BaseAttrInfoMapper baseAttrInfoMapper;

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @Override
    public BaseAttrInfo findById(Long id) {
        return baseAttrInfoMapper.selectById(id);
    }

    /**
     * 查询所有
     *
     * @return
     */
    @Override
    public List<BaseAttrInfo> findALL() {
        return baseAttrInfoMapper.selectList(null);
    }

    /**
     * 新增
     *
     * @param baseAttrInfo
     */
    @Override
    public void add(BaseAttrInfo baseAttrInfo) {
        //校验name是否为空
        if (StringUtils.isEmpty(baseAttrInfo.getAttrName())){
            throw new RuntimeException("参数错误,名字不能为空");
        }
        //新增属性
        int insert = baseAttrInfoMapper.insert(baseAttrInfo);
        //是否保存成功
        if (insert <= 0) {
            throw new RuntimeException("新增失败,请重试");
        }
    }

    /**
     * 修改
     *
     * @param baseAttrInfo
     */
    @Override
    public void update(BaseAttrInfo baseAttrInfo) {
        //校验名字是否为空
        if (StringUtils.isEmpty(baseAttrInfo.getAttrName())) {
            throw new RuntimeException("参数错误,名字不能为空");
        }
        //修改属性
        int update = baseAttrInfoMapper.updateById(baseAttrInfo);
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
        //删除属性
        int delete = baseAttrInfoMapper.deleteById(id);
        if (delete < 0){
            throw new RuntimeException("删除失败,请重试");
        }
    }

    /**
     * 条件查询
     *
     * @param baseAttrInfo
     * @return
     */
    @Override
    public List<BaseAttrInfo> search(BaseAttrInfo baseAttrInfo) {
        //参数校验
        if (baseAttrInfo == null) {
            //没有参数查所有
            return baseAttrInfoMapper.selectList(null);
        }
        //拼接条件
        LambdaQueryWrapper<BaseAttrInfo> wrapper = buildWrapper(baseAttrInfo);
        //根据条件查询数据库
        return baseAttrInfoMapper.selectList(wrapper);
    }

    /**
     * 分页查询
     *  @param page
     * @param size
     * @return
     */
    @Override
    public IPage<BaseAttrInfo> page(Integer page, Integer size) {
        return baseAttrInfoMapper.selectPage(new Page<BaseAttrInfo>(page,size), null);
    }

    /**
     * 条件分页查询
     *
     * @param page
     * @param size
     * @param baseAttrInfo
     * @return
     */
    @Override
    public IPage<BaseAttrInfo> search(Integer page, Integer size, BaseAttrInfo baseAttrInfo) {
        //校验条件
        if (baseAttrInfo == null){
            //查所有
            return baseAttrInfoMapper.selectPage(new Page<>(page,size),null);
        }
        //条件[拼接
        LambdaQueryWrapper<BaseAttrInfo> wrapper = buildWrapper(baseAttrInfo);
        return baseAttrInfoMapper.selectPage(new Page<>(page, size),wrapper);
    }

    /**
     * 条件拼接
     * @param baseAttrInfo
     * @return
     */
    private LambdaQueryWrapper<BaseAttrInfo> buildWrapper(BaseAttrInfo baseAttrInfo) {
        //条件构造器
        LambdaQueryWrapper<BaseAttrInfo> wrapper = new LambdaQueryWrapper<>();
        //根据id
        if (baseAttrInfo.getId() != null){
            wrapper.eq(BaseEntity::getId, baseAttrInfo.getId());
        }
        //根据name
        if (!StringUtils.isEmpty(baseAttrInfo.getAttrName())){
            wrapper.like(BaseAttrInfo::getAttrName, baseAttrInfo.getAttrName());
        }
        //根据分类id
        if (baseAttrInfo.getCategoryId() != null){
            wrapper.eq(BaseAttrInfo::getCategoryId, baseAttrInfo.getCategoryId());
        }
        //根据分类级别
        if (baseAttrInfo.getCategoryLevel() != null){
            wrapper.eq(BaseAttrInfo::getCategoryLevel, baseAttrInfo.getCategoryLevel());
        }
        return wrapper;
    }
}
