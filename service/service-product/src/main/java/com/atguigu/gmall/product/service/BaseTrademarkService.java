package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseTrademark;

/**
 * Date:2022/8/5
 * Author:cjc
 * Description:品牌信息接口
 */
public interface BaseTrademarkService {
    /**
     * 根据品牌id获取品牌信息
     * @param tmId
     * @return
     */
    public BaseTrademark getTrademarkById(Long tmId);
}
