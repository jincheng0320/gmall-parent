package com.atguigu.gmall.product.feign;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Date:2022/8/3
 * Author:cjc
 * Description:首页信息远程调用feign接口
 */
@FeignClient(name = "service-product", path = "/admin/product", contextId = "IndexFeign")
public interface IndexFeign {
    /**
     * 获取首页分类信息
     * @return
     */
    @GetMapping("/getCategory")
    public List<JSONObject> getCategory();
}
