package com.atguigu.gmall.item.controller;


import com.atguigu.gmall.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Date:2022/8/1
 * Author:cjc
 * Description:用户查询商品详情接口
 */
@RestController
@RequestMapping("/item")
public class ItemController {
    @Autowired
    private ItemService itemService;

    /**
     * 获取商品详情信息
     * @param skuId
     * @return
     */
    @GetMapping("/getItemInfo/{skuId}")
    public Map<String, Object> getItemInfo(@PathVariable("skuId") Long skuId){
        return itemService.getItemInfo(skuId);
    }
}
