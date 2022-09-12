package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.user.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Date:2022/8/8
 * Author:cjc
 * Description:用户认证控制层
 */
@RestController
@RequestMapping("/api/user")
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    /**
     * 根据用户名获取收货地址
     * @param
     * @return
     */
    @GetMapping("/getUserAddress")
    public Result getUserAddress(){
        return Result.ok(userAddressService.getUserAddress());
    }
}
