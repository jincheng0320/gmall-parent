package com.atguigu.gmall.list.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.service.GoodsService;
import com.atguigu.gmall.model.list.Goods;
import org.elasticsearch.client.ElasticsearchClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Date:2022/8/5
 * Author:cjc
 * Description:搜索控制层
 */
@RestController
@RequestMapping("/api/list")
public class ListController {
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * 创建索引及映射
     * @return
     */
    @GetMapping(value = "/create")
    public Result create(){
        //创建索引
        elasticsearchRestTemplate.createIndex(Goods.class);
        //创建映射
        elasticsearchRestTemplate.putMapping(Goods.class);
        return Result.ok();
    }

    @Autowired
    private GoodsService goodsService;
    /**
     * 上架商品 到es
     * @param skuId
     * @return
     */
    @GetMapping("/add/{skuId}")
    public Boolean add(@PathVariable("skuId") Long skuId){
        goodsService.addGoodsToEs(skuId);
        return true;
    }

    /**
     * 从es删除商品
     * @param goodsId
     * @return
     */
    @GetMapping("/remove/{goodsId}")
    public Boolean remove(@PathVariable("goodsId") Long goodsId){
        goodsService.removeFromEs(goodsId);
        return true;
    }

    /**
     * 增加热度
     * @param goodsId
     * @return
     */
    @GetMapping("/addHotScore/{goodsId}")
    public Result addHotScore(@PathVariable("goodsId") Long goodsId){
        goodsService.addHotScore(goodsId);
        return Result.ok();
    }

}
