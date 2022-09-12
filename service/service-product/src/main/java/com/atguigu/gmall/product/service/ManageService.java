package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * Date:2022/7/27
 * Author:cjc
 * Description:分类与属性的管理接口
 */
public interface ManageService {
    /**
     * 查询所有一级分类
     * @return
     */
    public List<BaseCategory1> getCategory1List();

    /**
     * 根据一级分类id查询二级分类
     * @return
     */
    public List<BaseCategory2> getCategory2List(Long category1Id);

    /**
     * 根据二级分类id查询三级分类
     * @return
     */
    public List<BaseCategory3> getCategory3List(Long category2Id);

    /**
     * 保存平台属性
     * @param baseAttrInfo
     */
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 根据分类id获取平台属性
     * @return
     */
    public List<BaseAttrInfo> getBaseAttrInfo(Long category1Id,
                                              Long category2Id,
                                              Long category3Id);

    /**
     * 根据平台id获取属性值
     * @param attrId
     * @return
     */
    public List<BaseAttrValue> getBaseAttrValue(Long attrId);

    /**
     * 品牌分页查询
     * @param page
     * @param size
     * @return
     */
    public IPage<BaseTrademark> getBaseTrademark(Integer page, Integer size);

    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    public BaseTrademark getBaseTrademarkById(Long id);

    /**
     * 添加品牌
     * @param baseTrademark
     */
    public void saveBaseTrademark(BaseTrademark baseTrademark);

    /**
     * 修改品牌
     * @param baseTrademark
     */
    public void updateBaseTrademark(BaseTrademark baseTrademark);

    /**
     * 删除品牌
     * @param id
     */
    public void deleteBaseTrademark(Long id);

    /**
     * 根据三三级分类id分页查询spuInfo信息
     * @param categories3Id
     * @param page
     * @param size
     * @return
     */
    public IPage<SpuInfo> getSpuInfoList(Long categories3Id,
                                         Integer page,
                                         Integer size);

    /**
     * 添加或修改spu
     * @param spuInfo
     */
    public void saveSpuInfo(SpuInfo spuInfo);

    /**
     * 获取销售属性
     * @return
     */
    public List<BaseSaleAttr> getBaseSaleAttr();

    /**
     * 获取品牌属性
     * @return
     */
    public List<BaseTrademark> getTrademarkList();

    /**
     * 根据spuId获取图片列表
     * @param supId
     * @return
     */
    public List<SpuImage> spuImageList(Long supId);

    /**
     * 根据spu的id获取spu的销售属性各信息
     * @param spuId
     */
    public List<SpuSaleAttr> spuSaleAttrList(Long spuId);

    /**
     * 添加或修改 sku
     * @param skuInfo
     */
    public void saveSkuInfo(SkuInfo skuInfo);

    /**
     * sku分页查询
     * @param page
     * @param size
     * @return
     */
    public IPage<SkuInfo> list(Integer page, Integer size);

    /**
     * 上架或者下架
     * @param skuId
     * @param status
     */
    public void upOrDown(Long skuId, Short status);
}
