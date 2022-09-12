package com.atguigu.gmall.item.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * Date:2022/8/3
 * Author:cjc
 * Description:商品详情远程调用
 */
@FeignClient(name = "service-item", path = "/item")
public interface ItemFeign {
    /**
     * 获取商品详情信息
     * @param skuId
     * @return
     */
    @GetMapping("/getItemInfo/{skuId}")
    public Map<String, Object> getItemInfo(@PathVariable("skuId") Long skuId);
}
