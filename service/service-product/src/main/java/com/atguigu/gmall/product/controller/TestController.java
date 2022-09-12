package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Date:2022/8/2
 * Author:cjc
 * Description:
 */
@RestController
@RequestMapping("/admin/product")
public class TestController {
    @Autowired
    private TestService testService;

    @GetMapping("/test")
    public Result setRedis(){
        testService.setRedission();
        return Result.ok();
    }
}
