package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Date:2022/7/29
 * Author:cjc
 * Description:商品销售属性映射
 */
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {
    /**
     * 根据spuId获取spu销售属性 和 spu销售属性值
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> spuSaleAttrList(@Param("spuId") Long spuId);

    /**
     * 根据spuid和skuid查询商品的销售属性信息并且标识出当前sku的销售属性是哪几个
     * @param skuId
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> spuSaleAttrListBySpuIdAndSkuId(@Param("skuId") Long skuId,
                                                     @Param("spuId") Long spuId);
}
