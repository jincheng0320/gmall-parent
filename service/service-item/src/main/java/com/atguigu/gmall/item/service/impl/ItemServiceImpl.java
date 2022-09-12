package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.feign.ItemFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Date:2022/8/1
 * Author:cjc
 * Description:商品详情微服务实现类
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemFeign itemFeign;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    /**
     * 获取商品详情所有信息
     *
     * @param skuId
     * @return
     */
    @Override
    public Map<String, Object> getItemInfo(Long skuId) {
        //参数校验
        if (skuId == null) {
            throw new RuntimeException("商品不存在");
        }
        //返回结果初始化
        Map<String, Object> map = new ConcurrentHashMap<>();
        //主线程执行查询skuInfo
        CompletableFuture<SkuInfo> future1 = CompletableFuture.supplyAsync(()->{
            //查询sku_info信息
            SkuInfo skuInfo = itemFeign.getSkuInfo(skuId);
            //判断sku是否存在
            if (skuInfo == null || skuInfo.getId() == null) {
                return null;
            }
            map.put("skuInfo",skuInfo);
            return skuInfo;
        }, threadPoolExecutor);
        //异步查询分类信息
        CompletableFuture<Void> future2 = future1.thenAcceptAsync(skuInfo -> {
            //判断skuInfo是否为空
            if (skuInfo == null){
                return;
            }
            //查询分类信息
            BaseCategoryView baseCategoryView = itemFeign.getCategoryView(skuInfo.getCategory3Id());
            map.put("baseCategoryView",baseCategoryView);
        }, threadPoolExecutor);
        //异步查询图片信息
        CompletableFuture<Void> future3 = future1.thenAcceptAsync(skuInfo -> {
            //判断skuInfo是否为空
            if (skuInfo == null) {
                return;
            }
            //查询图片信息
            List<SkuImage> imageList = itemFeign.getImageList(skuId);
            map.put("imageList", imageList);
        }, threadPoolExecutor);
        //异步查询价格信息
        CompletableFuture<Void> future4 = future1.thenAcceptAsync(skuInfo -> {
            //判断skuInfo是否为空
            if (skuInfo == null) {
                return;
            }
            //查询价格信息
            BigDecimal prices = itemFeign.getPrices(skuId);
            map.put("prices", prices);
        }, threadPoolExecutor);
        //异步查询销售属性信息
        CompletableFuture<Void> future5 = future1.thenAcceptAsync(skuInfo -> {
            //判断skuInfo是否为空
            if (skuInfo == null) {
                return;
            }
            //查询销售属性信息
            List<SpuSaleAttr> spuSaleAttrList = itemFeign.getSpuSaleAttr(skuId, skuInfo.getSpuId());
            map.put("spuSaleAttrList", spuSaleAttrList);
        }, threadPoolExecutor);
        //异步查询指定spu的所有skuId:销售属性键值对
        CompletableFuture<Void> future6 = future1.thenAcceptAsync(skuInfo -> {
            //判断skuInfo是否为空
            if (skuInfo == null) {
                return;
            }
            //查询指定spu的所有skuId:销售属性键值对
            Map keyAndValues = itemFeign.getSkuIdAndValues(skuInfo.getSpuId());
            map.put("keyAndValues", keyAndValues);
        }, threadPoolExecutor);
        //等待所有任务完成并返回结果
        CompletableFuture.allOf(future1, future2, future3, future4, future5, future6).join();
        //合并返回
        return map;
    }
}
