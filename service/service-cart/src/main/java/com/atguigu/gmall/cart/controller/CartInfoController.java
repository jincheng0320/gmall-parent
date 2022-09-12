package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartInfoService;
import com.atguigu.gmall.common.constant.CartConst;
import com.atguigu.gmall.common.result.Result;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Date:2022/8/9
 * Author:cjc
 * Description:购物车控制层
 */
@RestController
@RequestMapping("/api/cart")
public class CartInfoController {
    @Autowired
    private CartInfoService cartInfoService;

    /**
     * 添加购物车
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping("/addCart")
    public Result addCart(Long skuId,Integer num){
        cartInfoService.addCartInfo(skuId,num);
        return Result.ok();
    }

    /**
     * 查询用户购物车
     * @return
     */
    @GetMapping("/showCart")
    public Result showCart(){
        return Result.ok(cartInfoService.showCart());
    }

    /**
     * 删除购物车
     * @param skuId
     * @return
     */
    @GetMapping("/removeCart")
    public Result removeCart(Long skuId){
        cartInfoService.removeCart(skuId);
        return Result.ok();
    }

    /**
     * 购物车商品选中
     * @param id
     * @return
     */
    @GetMapping("/check")
    public Result check(Long id){
        cartInfoService.checkOrUncheck(id, CartConst.CART_CHECK);
        return Result.ok();
    }

    /**
     * 购物车商品取消选中
     * @param id
     * @return
     */
    @GetMapping("/uncheck")
    public Result uncheck(Long id){
        cartInfoService.checkOrUncheck(id, CartConst.CART_UNCHECK);
        return Result.ok();
    }

    /**
     * 获取本次购物车总金额总数量和购物车列表
     * @return
     */
    @GetMapping("/getOrderComfirm")
    public Result getOrderComfirm(){
        return Result.ok(cartInfoService.getOrderComfirm());
    }
}
