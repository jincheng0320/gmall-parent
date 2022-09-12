package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartInfoService;
import com.atguigu.gmall.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Date:2022/8/10
 * Author:cjc
 * Description:内部调用购物车接口
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartInfoService cartInfoService;

    /**
     * 获取本次购物车总金额总数量和购物车列表
     * @return
     */
    @GetMapping("/getOrderComfirm")
    public Map<String,Object> getOrderComfirm(){
        return cartInfoService.getOrderComfirm();
    }

    /**
     * 清空购物车
     * @return
     */
    @GetMapping("/removeCart")
    public void removeCart(){
        cartInfoService.clearCart();
    }
}
