package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Date:2022/7/26
 * Author:cjc
 * Description:
 */
@RestController
@RequestMapping("/product/BaseAttrValue")
public class BaseAttrValueController {
    //注入service
    @Autowired
    private BaseAttrValueService baseAttrValueService;

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/findById/{id}")
    public Result findById(@PathVariable("id") Long id){
        return Result.ok(baseAttrValueService.findById(id));
    }

    /**
     * 查询所有
     * @return
     */
    @GetMapping("/findAll")
    public Result findAll(){
        return Result.ok(baseAttrValueService.findAll());
    }

    /**
     * 新增
     * @param baseAttrValue
     * @return
     */
    @PostMapping
    public Result add(@RequestBody BaseAttrValue baseAttrValue){
        baseAttrValueService.add(baseAttrValue);
        return Result.ok();
    }

    /**
     * 修改
     * @param baseAttrValue
     * @return
     */
    @PutMapping
    public Result update(@RequestBody BaseAttrValue baseAttrValue){
        baseAttrValueService.update(baseAttrValue);
        return Result.ok();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable("id") Long id){
        baseAttrValueService.delete(id);
        return Result.ok();
    }

    /**
     * 条件查询
     * @param baseAttrValue
     * @return
     */
    @PostMapping("/search")
    public Result search(@RequestBody BaseAttrValue baseAttrValue){
        return Result.ok(baseAttrValueService.search(baseAttrValue));
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/page/{page}/{size}")
    public Result page(@PathVariable("page") Integer page,
                       @PathVariable("size") Integer size){
        return Result.ok(baseAttrValueService.page(page,size));
    }

    /**
     * 分页带条件查询
     * @param page
     * @param size
     * @param baseAttrValue
     * @return
     */
    @PostMapping("/search/{page}/{size}")
    public Result search(@PathVariable("page") Integer page,
                         @PathVariable("size") Integer size,
                         @RequestBody BaseAttrValue baseAttrValue){
        return Result.ok(baseAttrValueService.search(page, size, baseAttrValue));
    }
}
