package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.list.feign.SearchFeign;
import com.atguigu.gmall.web.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Date:2022/8/7
 * Author:cjc
 * Description:搜索页面,前端控制成
 */
@Controller
@RequestMapping("/page/search")
public class SearchController {

    @Autowired
    private SearchFeign searchFeign;
    @Value("${item.url}")
    private String itemUrl;
    /**
     * 打开搜索页面
     * @param searchData
     * @param model
     * @return
     */
    @GetMapping
    public String search(@RequestParam Map<String, String> searchData, Model model){
        //搜索结果
        Map<String, Object> searchResult = searchFeign.search(searchData);
        //存储
        model.addAllAttributes(searchResult);
        //查询条件回显
        model.addAttribute("searchData",searchData);
        //获取当前url
        model.addAttribute("url",getUrl(searchData));
        //获取排序的url
        model.addAttribute("sortUrl",  getSortUrl(searchData));
        //获取总数量
        Object totalHits = searchResult.get("totalHits");
        //获取页码
        String pageNum = searchData.get("pageNum");
        //初始化分页工具类
        Page pageInfo = new Page<>(
                Long.valueOf(totalHits.toString()),
                getPage(pageNum),50);
        model.addAttribute("pageInfo", pageInfo);
        //存放商品详情页的前缀域名
        model.addAttribute("itemUrl", itemUrl);
        //打开页面
        return "list1";
    }

    /**
     * 拼接当前url
     * @param searchData
     * @return
     */
    private String getUrl(Map<String, String> searchData) {
        //初始化
        String url = "/page/search?";
        //遍历拼接
        for (Map.Entry<String, String> entry : searchData.entrySet()) {
            //获取名字
            String key = entry.getKey();
            if (!key.equals("pageNum")){
                //获取值
                String value = entry.getValue();
                //拼接
                url = url+key+"="+value+"&";
            }
        }
        //返回
        return url.substring(0, url.length()-1);
    }

    /**
     * 获取当前不包含排序url
     * @param searchData
     * @return
     */
    private String getSortUrl(Map<String, String> searchData) {
        //初始化
        String url = "/page/search?";
        //遍历拼接
        for (Map.Entry<String, String> entry : searchData.entrySet()) {
            //获取名字
            String key = entry.getKey();
            if (!key.equals("sortField") && !key.equals("sortRule") && !key.equals("pageNum")){
                //获取值
                String value = entry.getValue();
                //拼接
                url = url+key+"="+value+"&";
            }
        }
        //返回
        return url.substring(0, url.length()-1);
    }

    /**
     * 计算页码
     * @param pageNum
     * @return
     */
    private int getPage(String pageNum) {
        try{
            int i = Integer.parseInt(pageNum);
            return i>0?i:1;
        }catch (Exception e){
            return 1;
        }
    }
}
