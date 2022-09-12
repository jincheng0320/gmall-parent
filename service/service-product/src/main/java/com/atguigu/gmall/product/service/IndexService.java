package com.atguigu.gmall.product.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * Date:2022/8/3
 * Author:cjc
 * Description:首页相关接口
 */
public interface IndexService {
    /**
     * 获取首页分类信息
     * @return
     */
    public List<JSONObject> getIndexCategory();
}
