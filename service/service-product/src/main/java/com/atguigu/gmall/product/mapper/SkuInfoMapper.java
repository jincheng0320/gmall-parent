package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * Date:2022/7/30
 * Author:cjc
 * Description:商品详情映射
 */
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {
    /**
     * 扣除库存
     */
    @Update("update sku_info set stock=stock-#{num} where id = #{skuId} and stock>#{num}")
    public int reduceStock(@Param("skuId") Long skuId,
                           @Param("num") Integer num);

    @Update("update sku_info set stock=stock+ #{num} where id = #{skuId}")
    public int rollbackStock(@Param("skuId") Long skuId,
                             @Param("num") Integer num);
}
