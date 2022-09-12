package com.atguigu.gmall.item.service;

import java.util.Map;

/**
 * Date:2022/8/1
 * Author:cjc
 * Description:商品详情微服务接口
 */
public interface ItemService {
    /**
     * 获取商品详情所有信息
     * @param skuId
     * @return
     */
    public Map<String, Object> getItemInfo(Long skuId);

}
