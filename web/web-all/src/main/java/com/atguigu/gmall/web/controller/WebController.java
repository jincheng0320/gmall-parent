package com.atguigu.gmall.web.controller;


import com.atguigu.gmall.item.feign.ItemFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Date:2022/8/3
 * Author:cjc
 * Description:商品详情前端控制层
 */
@Controller
@RequestMapping("/page/item")
public class WebController {
    @Autowired
    private ItemFeign itemFeign;

    /**
     * 前端商品详情
     * @return
     */
    @GetMapping("/{skuId}")
    public String item(Model model,
                       @PathVariable("skuId") Long skuId){
        //远程调用商品详情微服务
        Map<String, Object> result = itemFeign.getItemInfo(skuId);
        //将结果存储model
        model.addAllAttributes(result);
        return "item1";
    }

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * 创建商品静态页面
     * @param skuId
     * @return
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    @GetMapping("/createHtml/{skuId}")
    @ResponseBody
    public String createHtml(@PathVariable("skuId") Long skuId) throws FileNotFoundException, UnsupportedEncodingException {
        //查询商品详情
        Map<String, Object> result = itemFeign.getItemInfo(skuId);
        //创建一个输出流
        File file = new File("D:\\"+skuId+".html");
        PrintWriter printWriter = new PrintWriter(file,"UTF-8");
        //初始话数据容器
        Context context = new Context();
        context.setVariables(result);
        //将数据写入静态页面
        templateEngine.process("item2", context, printWriter);
        printWriter.flush();
        printWriter.close();
        //返回
        return "创建成功";
    }
}
