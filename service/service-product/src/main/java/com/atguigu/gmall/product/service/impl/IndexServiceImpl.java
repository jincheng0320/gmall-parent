package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.product.mapper.BaseCategoryViewMapper;
import com.atguigu.gmall.product.service.IndexService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Date:2022/8/3
 * Author:cjc
 * Description:
 */
@Service
public class IndexServiceImpl implements IndexService {
    @Resource
    private BaseCategoryViewMapper baseCategoryViewMapper;

    /**
     * 获取首页分类信息
     * @return
     */
    @Override
    public List<JSONObject> getIndexCategory() {
        //查询所有分类 1/2/3
        List<BaseCategoryView> baseCategoryViewList1 = baseCategoryViewMapper.selectList(null);
        //以一级分类进行分组
        Map<Long, List<BaseCategoryView>> category1Map = baseCategoryViewList1.stream().
                collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        //遍历
       return category1Map.entrySet().stream().map(category1Entry -> {
            //返回结果初始化
            JSONObject category1Json = new JSONObject();
            //获取每个一级分类id
            Long category1Id = category1Entry.getKey();
            category1Json.put("categoryId", category1Id);
            //获取每个一级分类对应的二级分类/三级分类列表
            List<BaseCategoryView> baseCategoryViewList2 = category1Entry.getValue();
            //获取一级分类name
            String category1Name = baseCategoryViewList2.get(0).getCategory1Name();
            category1Json.put("categoryName", category1Name);
            //再以二级分类进行分组 获取二级分类对应三级分类
            Map<Long, List<BaseCategoryView>> category2Map = baseCategoryViewList2.stream().
                    collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            //遍历
            List<JSONObject> category2JsonList = category2Map.entrySet().stream().map(category2Entry -> {
                //返回结果初始化
                JSONObject category2Json = new JSONObject();
                //获取每个二级分类id
                Long category2Id = category2Entry.getKey();
                category2Json.put("categoryId", category2Id);
                //获取每个二级分类对应的三级分类列
                List<BaseCategoryView> baseCategoryViewList3 = category2Entry.getValue();
                //获取二级分类name
                String category2Name = baseCategoryViewList3.get(0).getCategory2Name();
                category2Json.put("categoryName", category2Name);
                //遍历三级分类获取信息
                List<JSONObject> category3JsonList = baseCategoryViewList3.stream().map(categoryView3 -> {
                    //返回结果初始化
                    JSONObject category3Json = new JSONObject();
                    //获取三级分类id
                    Long category3Id = categoryView3.getCategory3Id();
                    category3Json.put("categoryId", category3Id);
                    //获取三级分类name
                    String category3Name = categoryView3.getCategory3Name();
                    category3Json.put("categoryName", category3Name);
                    //返回category信息
                    return category3Json;
                }).collect(Collectors.toList());
                //保存二级分类对应的三级分类
                category2Json.put("childCategory", category3JsonList);
                //返回
                return category2Json;
            }).collect(Collectors.toList());
            //保存一级分类对应的二级分类
           category1Json.put("childCategory", category2JsonList);
            //返回
            return category1Json;
        }).collect(Collectors.toList());
    }
}
