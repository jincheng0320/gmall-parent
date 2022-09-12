package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Date:2022/7/26
 * Author:cjc
 * Description:平台属性表的Mapper映射
 */
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {
    /**
     * 根据categoriesId获取平台属性
     * @param categories1
     * @param categories2
     * @param categories3
     * @return
     */
    List<BaseAttrInfo> selectBaseAttrInfoByCategoryId(@Param("categories1") Long categories1,
                                                      @Param("categories2") Long categories2,
                                                      @Param("categories3") Long categories3);

    /**
     * 根据skuId获取平台属性键值对
     * @param skuId
     * @return
     */
    List<BaseAttrInfo> selectBaseAttrInfoBySkuId(@Param("skuId") Long skuId);

}
