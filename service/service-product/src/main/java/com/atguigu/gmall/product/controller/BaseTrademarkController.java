package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Date:2022/8/5
 * Author:cjc
 * Description:
 */
@RestController
@RequestMapping("/api/Trademark")
public class BaseTrademarkController {
    @Autowired
    private BaseTrademarkService baseTrademarkService;

    /**
     * 根据id查询品牌信息
     * @param tmId
     * @return
     */
    @GetMapping("/getTrademarkById/{tmId}")
    public Result getTrademarkById(@PathVariable("tmId") Long tmId){

        return Result.ok(baseTrademarkService.getTrademarkById(tmId));
    }
}
