package com.atguigu.gmall.list.service;

import java.util.Map;

/**
 * Date:2022/8/6
 * Author:cjc
 * Description:搜索相关的接口类
 */
public interface SearchService {

    /**
     * 关键字搜索
     * @param searchData
     * @return
     */
    public Map<String, Object> Search(Map<String, String> searchData);
}
