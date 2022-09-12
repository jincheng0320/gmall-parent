package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Date:2022/7/30
 * Author:cjc
 * Description:sku销售属性值表的映射
 */
public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {
    /**
     * 根据spu查询所有销售属性键值对
     * @param spuId
     * @return
     */
    public List<Map> selectSaleAttrKeyValueBySpuId(@Param("spuId") Long spuId);
}
