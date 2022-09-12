package com.atguigu.gmall.list.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Date:2022/8/5
 * Author:cjc
 * Description:搜索信息远程调用接口
 */
@FeignClient(name = "service-list", path = "/api/list", contextId = "ListFeign")
public interface ListFeign {
    /**
     * 上架商品 到es
     * @param skuId
     * @return
     */
    @GetMapping("/add/{skuId}")
    public Boolean add(@PathVariable("skuId") Long skuId);

    /**
     * 从es删除商品
     * @param goodsId
     * @return
     */
    @GetMapping("/remove/{goodsId}")
    public Boolean remove(@PathVariable("goodsId") Long goodsId);
}
