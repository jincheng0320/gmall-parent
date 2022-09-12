package com.atguigu.gmall.cart.feign;

import com.atguigu.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * Date:2022/8/10
 * Author:cjc
 * Description:购物车远程调用
 */
@FeignClient(name = "service-cart", path = "/cart", contextId = "CartInfoFeign")
public interface CartInfoFeign {
    /**
     * 获取本次购物车总金额总数量和购物车列表
     * @return
     */
    @GetMapping("/getOrderComfirm")
    public Map<String,Object> getOrderComfirm();

    /**
     * 清空购物车
     * @return
     */
    @GetMapping("/removeCart")
    public void removeCart();
}
