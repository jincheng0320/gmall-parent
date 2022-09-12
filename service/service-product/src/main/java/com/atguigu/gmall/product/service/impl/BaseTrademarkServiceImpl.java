package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Date:2022/8/5
 * Author:cjc
 * Description:品牌接口实现类
 */
@Service
public class BaseTrademarkServiceImpl implements BaseTrademarkService {
    @Resource
    private BaseTrademarkMapper baseTrademarkMapper;
    /**
     * 根据品牌id获取品牌信息
     *
     * @param tmId
     * @return
     */
    @Override
    public BaseTrademark getTrademarkById(Long tmId) {
        return baseTrademarkMapper.selectById(tmId);
    }
}
