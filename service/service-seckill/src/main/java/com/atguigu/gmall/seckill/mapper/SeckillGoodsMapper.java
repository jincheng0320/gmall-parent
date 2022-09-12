package com.atguigu.gmall.seckill.mapper;

import com.atguigu.gmall.model.activity.SeckillGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * Date:2022/8/15
 * Author:cjc
 * Description:秒杀商品的映射
 */
public interface SeckillGoodsMapper extends BaseMapper<SeckillGoods> {

    @Update("update seckill_goods set stock_count=#{stock} where id = #{goodsId}")
    public int updateStock(@Param("goodsId") long goodsId,
                           @Param("stock") Integer stock);
}
