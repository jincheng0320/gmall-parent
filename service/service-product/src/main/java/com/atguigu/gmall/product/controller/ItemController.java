package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.cache.Java0217Cache;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Date:2022/8/1
 * Author:cjc
 * Description:内部调用控制层
 */
@RestController
@RequestMapping("/api/item")
public class ItemController {
    @Autowired
    private ItemService itemService;

    /**
     * 查询sku信息
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuInfo/{skuId}")
    @Java0217Cache(prefix = "getSkuInfo:")
    public SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId){
        return itemService.getSkuInfo(skuId);
    }

    /**
     * 查询分类信息
     * @param category3Id
     * @return
     */
    @GetMapping("/getCategoryView/{category3Id}")
    @Java0217Cache(prefix = "getCategoryView:")
    public BaseCategoryView getCategoryView(@PathVariable("category3Id") Long category3Id){
        return itemService.getCategoryViewByCategory3Id(category3Id);
    }

    /**
     * 获取图片列表
     * @param skuId
     * @return
     */
    @GetMapping("/getImageList/{skuId}")
    @Java0217Cache(prefix = "getImageList:")
    public List<SkuImage> getImageList(@PathVariable("skuId") Long skuId){
        return itemService.getImageList(skuId);
    }

    /**
     * 获取商品价格
     * @param skuId
     * @return
     */
    @GetMapping("/getPrices/{skuId}")
    @Java0217Cache(prefix = "getPrices:")
    public BigDecimal getPrices(@PathVariable("skuId") Long skuId){
        return itemService.getPrice(skuId);
    }

    /**
     * 根据spuid和skuid查询商品的销售属性信息并且标识出当前sku的销售属性是哪几个
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping("/getSpuSaleAttr/{skuId}/{spuId}")
    @Java0217Cache(prefix = "getSpuSaleAttr:")
    public List<SpuSaleAttr> getSpuSaleAttr(@PathVariable("skuId") Long skuId,
                                            @PathVariable("spuId") Long spuId){
        return itemService.getSpuSaleAttr(skuId, spuId);
    }

    /**
     * 根据spuId查询键值对
     * @param spuId
     * @return
     */
    @GetMapping("/getSkuIdAndValues/{spuId}")
    @Java0217Cache(prefix = "getSkuIdAndValues:")
    public Map getSkuIdAndValues(@PathVariable("spuId") Long spuId){
        return itemService.getSkuIdAndValues(spuId);
    }

    /**
     * 根据品牌id查询品牌信息
     * @param tmId
     * @return
     */
    @GetMapping("/getTrademarkById/{tmId}")
    public BaseTrademark getTrademarkById(@PathVariable("tmId") Long tmId){
        return itemService.getTrademarkById(tmId);
    }

    /**
     * 根据skuId查询平台属性键值对
     * @param skuId
     * @return
     */
    @GetMapping("/getBaseAttrInfo/{skuId}")
    public List<BaseAttrInfo> getBaseAttrInfo(@PathVariable("skuId") Long skuId){
        return itemService.getBaseAttrInfo(skuId);
    }

    /**
     * 扣减库存
     * @param skuParam
     */
    @GetMapping("/reduceStock")
    public void reduceStock(@RequestParam Map<String, Object> skuParam){
        itemService.reduceStock(skuParam);
    }

    /**
     * 回滚库存
     * @param skuParam
     */
    @GetMapping("/rollbackStock")
    public void rollbackStock(@RequestParam Map<String, Object> skuParam){
        itemService.rollbackStock(skuParam);
    }
}
