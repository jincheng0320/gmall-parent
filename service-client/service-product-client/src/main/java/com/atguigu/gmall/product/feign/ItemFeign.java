package com.atguigu.gmall.product.feign;

import com.atguigu.gmall.model.product.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Date:2022/8/1
 * Author:cjc
 * Description:商品管理远程调用feign接口
 */
@FeignClient(name = "service-product", path = "/api/item", contextId = "ItemFeign")
public interface ItemFeign {

    /**
     * 查询sku信息
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId);

    /**
     * 查询分类信息
     * @param category3Id
     * @return
     */
    @GetMapping("/getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryView(@PathVariable("category3Id") Long category3Id);

    /**
     * 获取图片列表
     * @param skuId
     * @return
     */
    @GetMapping("/getImageList/{skuId}")
    public List<SkuImage> getImageList(@PathVariable("skuId") Long skuId);

    /**
     * 获取商品价格
     * @param skuId
     * @return
     */
    @GetMapping("/getPrices/{skuId}")
    public BigDecimal getPrices(@PathVariable("skuId") Long skuId);

    /**
     * 根据spuid和skuid查询商品的销售属性信息并且标识出当前sku的销售属性是哪几个
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping("/getSpuSaleAttr/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttr(@PathVariable("skuId") Long skuId,
                                            @PathVariable("spuId") Long spuId);

    /**
     * 根据spuId查询键值对
     * @param spuId
     * @return
     */
    @GetMapping("/getSkuIdAndValues/{spuId}")
    public Map getSkuIdAndValues(@PathVariable("spuId") Long spuId);

    /**
     * 根据品牌id查询品牌信息
     * @param tmId
     * @return
     */
    @GetMapping("/getTrademarkById/{tmId}")
    public BaseTrademark getTrademarkById(@PathVariable("tmId") Long tmId);

    /**
     * 根据skuId查询平台属性键值对
     * @param skuId
     * @return
     */
    @GetMapping("/getBaseAttrInfo/{skuId}")
    public List<BaseAttrInfo> getBaseAttrInfo(@PathVariable("skuId") Long skuId);

    /**
     * 扣减库存
     * @param skuParam
     */
    @GetMapping("/reduceStock")
    public void reduceStock(@RequestParam Map<String, Object> skuParam);

    /**
     * 回滚库存
     * @param skuParam
     */
    @GetMapping("/rollbackStock")
    public void rollbackStock(@RequestParam Map<String, Object> skuParam);
}
