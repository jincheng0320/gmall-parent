package com.atguigu.gmall.list.service;



/**
 * Date:2022/8/5
 * Author:cjc
 * Description:es中商品相关的接口
 */
public interface GoodsService {
    /**
     * 上架商品写入ES
     * @param skuId
     */
    public void addGoodsToEs(Long skuId);

    /**
     * 从ES中删除下架的商品
     * @param goodsId
     */
    public void removeFromEs(Long goodsId);

    /**
     * 为商品加热度
     */
    public void addHotScore(Long goodsId);
}
