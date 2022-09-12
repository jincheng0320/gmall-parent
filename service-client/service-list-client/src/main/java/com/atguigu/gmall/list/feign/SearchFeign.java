package com.atguigu.gmall.list.feign;

import com.atguigu.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Date:2022/8/7
 * Author:cjc
 * Description:
 */
@FeignClient(name = "service-list", path = "/api/search", contextId = "SearchFeign")
public interface SearchFeign {
    /**
     * 商品搜索
     * @param searchData
     * @return
     */
    @GetMapping
    public Map<String, Object> search(@RequestParam Map<String, String> searchData);
}
