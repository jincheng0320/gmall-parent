package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;


/**
 * Date:2022/7/26
 * Author:cjc
 * Description:
 */
@RestController
@RequestMapping("/product/baseAttrInfo")
public class BaseAttrInfoController {
    @Autowired
    private BaseAttrInfoService baseAttrInfoService;

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/findById/{id}")
    public Result findById(@PathVariable("id") Long id){
        return Result.ok(baseAttrInfoService.findById(id));
    }

    /**
     * 查询所有
     * @return
     */
    @GetMapping("/findAll")
    public Result finAll(){
        return Result.ok(baseAttrInfoService.findALL());
    }

    /**
     * 新增
     * @param baseAttrInfo
     * @return
     */
    @PostMapping
    public Result add(@RequestBody BaseAttrInfo baseAttrInfo){
        baseAttrInfoService.add(baseAttrInfo);
        return Result.ok();
    }

    /**
     * 修改
     * @param baseAttrInfo
     * @return
     */
    @PutMapping
    public Result update(@RequestBody BaseAttrInfo baseAttrInfo){
        baseAttrInfoService.update(baseAttrInfo);
        return Result.ok();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable("id") Long id){
        baseAttrInfoService.delete(id);
        return Result.ok();
    }

    /**
     * 条件查询
     * @param baseAttrInfo
     * @return
     */
    @PostMapping("/search")
    public Result search(@RequestBody BaseAttrInfo baseAttrInfo){
        List<BaseAttrInfo> search = baseAttrInfoService.search(baseAttrInfo);
        return Result.ok(search);
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
        return Result.ok(baseAttrInfoService.page(page, size));
    }

    /**
     * 分页条件查询
     * @param page
     * @param size
     * @param baseAttrInfo
     * @return
     */
    @PostMapping("/search/{page}/{size}")
    public Result search(@PathVariable("page") Integer page,
                         @PathVariable("size") Integer size,
                         @RequestBody BaseAttrInfo baseAttrInfo){
        return Result.ok(baseAttrInfoService.search(page, size, baseAttrInfo));
    }
}
