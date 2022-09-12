package com.atguigu.gmall.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.product.feign.IndexFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Date:2022/8/3
 * Author:cjc
 * Description:
 */
@Controller
@RequestMapping("/index")
public class IndexController {
    @Autowired
    private IndexFeign indexFeign;

    /**
     *
     * @param model
     * @return
     */
    @GetMapping
    public String index(Model model){
        //远程调用商品管理微服务查询分类信息
        List<JSONObject> categoryList = indexFeign.getCategory();
        //存到model
        model.addAttribute("categoryList", categoryList);
        return "index1";
    }
}
