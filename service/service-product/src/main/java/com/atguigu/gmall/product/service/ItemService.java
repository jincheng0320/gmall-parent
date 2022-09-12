package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Date:2022/8/1
 * Author:cjc
 * Description:内部调用接口
 */
public interface ItemService {
    /**
     * 获取商品详情
     * @param skuId
     * @return
     */
    public SkuInfo getSkuInfo(Long skuId);

    /**
     * 获取商品详情优化到缓存
     * @param skuId
     * @return
     */
    public SkuInfo getItemFromDbOrRedis(Long skuId);

    /**
     * 获取分类信息
     * @param category3Id
     * @return
     */
    public BaseCategoryView getCategoryViewByCategory3Id(Long category3Id);

    /**
     * 获取图片列表
     * @param skuId
     * @return
     */
    public List<SkuImage> getImageList(Long skuId);

    /**
     * 获取价格
     * @param skuId
     * @return
     */
    public BigDecimal getPrice(Long skuId);

    /**
     * 根据spuid和skuid查询商品的销售属性信息并且标识出当前sku的销售属性是哪几个
     * @param skuId
     * @param spuId
     * @return
     */
    public List<SpuSaleAttr> getSpuSaleAttr(Long skuId, Long spuId);

    /**
     * 根据spu查询所有sku销售属性和值 键值对
     * @param spuId
     * @return
     */
    public Map getSkuIdAndValues(Long spuId);

    /**
     * 根据品牌id查询品牌信息
     * @param tmId
     * @return
     */
    public BaseTrademark getTrademarkById(Long tmId);

    /**
     * 根据skuId查询平台属性键值对
     * @param skuId
     * @return
     */
    public List<BaseAttrInfo> getBaseAttrInfo(Long skuId);

    /**
     * 扣减库存
     * @param skuParam
     */
    public void reduceStock(Map<String, Object> skuParam);

    /**
     * 回滚库存
     * @param skuParam
     */
    public void rollbackStock(Map<String, Object> skuParam);
}
