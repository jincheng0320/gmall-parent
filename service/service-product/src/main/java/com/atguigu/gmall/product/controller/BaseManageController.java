package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.constant.ProductConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.ManageService;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

/**
 * Date:2022/7/27
 * Author:cjc
 * Description:
 */
@RestController
@RequestMapping("/admin/product")
public class BaseManageController {
    @Autowired
    private ManageService manageService;

    /**
     * 查询所有一级分类
     * @return
     */
    @GetMapping("/getCategory1")
    public Result getCategory1(){

        return Result.ok(manageService.getCategory1List());
    }

    /**
     * 根据一级分类id查询二级分类
     * @param category1Id
     * @return
     */
    @GetMapping("/getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable("category1Id") Long category1Id){
        return Result.ok(manageService.getCategory2List(category1Id));
    }

    /**
     * 根据二级分类id查询三级分类
     * @param category2Id
     * @return
     */
    @GetMapping("/getCategory3/{category2Id}")
    public Result getCategory3(@PathVariable Long category2Id){
        return Result.ok(manageService.getCategory3List(category2Id));
    }

    /**
     * 保存平台属性
     * @param baseAttrInfo
     * @return
     */
    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        manageService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }

    /**
     * 根据分类id获取平台属性
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoList(@PathVariable("category1Id") Long category1Id,
                               @PathVariable("category2Id") Long category2Id,
                               @PathVariable("category3Id") Long category3Id){
        return Result.ok(manageService.getBaseAttrInfo(category1Id, category2Id, category3Id));
    }

    /**
     * 根据平台属性id获取平台属性值
     * @param attrId
     * @return
     */
    @GetMapping("/getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable("attrId") long attrId){
        return Result.ok(manageService.getBaseAttrValue(attrId));
    }

    /**
     * 品牌分页查询
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/baseTrademark/{page}/{size}")
    public Result getBaseTrademark(@PathVariable("page") Integer page,
                                   @PathVariable("size") Integer size){
        return Result.ok(manageService.getBaseTrademark(page, size));
    }

    /**
     * 根据品牌id查询
     * @param id
     * @return
     */
    @GetMapping("/baseTrademark/get/{id}")
    public Result getBaseTrademarkById(@PathVariable("id") Long id){
        return Result.ok(manageService.getBaseTrademarkById(id));
    }

    /**
     * 添加品牌
     * @param baseTrademark
     * @return
     */
    @PostMapping("/baseTrademark/save")
    public Result saveBaseTrademark(@RequestBody BaseTrademark baseTrademark){
        manageService.saveBaseTrademark(baseTrademark);
        return Result.ok();
    }

    /**
     * 修改品牌
     * @param baseTrademark
     * @return
     */
    @PutMapping("/baseTrademark/update")
    public Result updateBaseTrademark(@RequestBody BaseTrademark baseTrademark){
        manageService.updateBaseTrademark(baseTrademark);
        return Result.ok();
    }

    /**
     * 删除品牌
     * @param id
     * @return
     */
    @DeleteMapping("/baseTrademark/remove/{id}")
    public Result removeBaseTrademark(@PathVariable("id") Long id){
        manageService.deleteBaseTrademark(id);
        return Result.ok();
    }

    /**
     * 根据三级分类id分页查询商品spuInfo
     * @param page
     * @param size
     * @param category3Id
     * @return
     */
    @GetMapping("/{page}/{size}")
    public Result getSpuInfoList(@PathVariable("page") Integer page,
                                 @PathVariable("size") Integer size,
                                 @PathParam("category3Id") Long category3Id){
        return Result.ok(manageService.getSpuInfoList(category3Id,page,size));
    }

    /**
     * 添加spu
     * @param spuInfo
     * @return
     */
    @PostMapping("/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        manageService.saveSpuInfo(spuInfo);
        return Result.ok();
    }

    /**
     * 获取销售属性
     * @return
     */
    @GetMapping("/baseSaleAttrList")
    public Result getBaseSaleAttrList(){
        return Result.ok(manageService.getBaseSaleAttr());
    }

    /**
     * 获取品牌属性
     * @return
     */
    @GetMapping("/baseTrademark/getTrademarkList")
    public Result getTrademarkList(){
        return Result.ok(manageService.getTrademarkList());
    }

    /**
     * 根据spuId获取图片列表
     * @param spuId
     * @return
     */
    @GetMapping("/spuImageList/{spuId}")
    public Result spuImageList(@PathVariable("spuId") Long spuId){
        return Result.ok(manageService.spuImageList(spuId));
    }

    /**
     * 根据spuId查询销售属性 销售属性值
     * @param supId
     * @return
     */
    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable("spuId") Long supId){
        return Result.ok(manageService.spuSaleAttrList(supId));
    }

    /**
     * sku分页
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list/{page}/{size}")
    public Result list(@PathVariable("page") Integer page,
                       @PathVariable("size") Integer size){
        return Result.ok(manageService.list(page, size));
    }

    /**
     * 添加 修改 skuInfo
     * @param skuInfo
     * @return
     */
    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){
        manageService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    /**
     * 上架
     * @param skuId
     * @return
     */
    @GetMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable("skuId") Long skuId){
        manageService.upOrDown(skuId, ProductConst.SKU_ON_SALE);
        return Result.ok();
    }

    /**
     * 下架
     * @param skuId
     * @return
     */
    @GetMapping("/cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId") Long skuId){
        manageService.upOrDown(skuId, ProductConst.SKU_CANCEL_SALE);
        return Result.ok();
    }
}
