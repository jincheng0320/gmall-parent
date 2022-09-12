package com.atguigu.gmall.product.controller;



import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.cache.Java0217Cache;
import com.atguigu.gmall.product.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Date:2022/8/3
 * Author:cjc
 * Description:前端页面使用接口
 */
@RestController
@RequestMapping("/admin/product")
public class IndexController {
    @Autowired
    private IndexService indexService;

    /**
     * 获取首页分类信息
     * @return
     */
    @GetMapping("/getCategory")
    @Java0217Cache(prefix = "getCategory")
    public List<JSONObject> getCategory(){
        return indexService.getIndexCategory();
    }
}
