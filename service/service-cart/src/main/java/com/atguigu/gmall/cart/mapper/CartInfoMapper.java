package com.atguigu.gmall.cart.mapper;

import com.atguigu.gmall.model.cart.CartInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * Date:2022/8/9
 * Author:cjc
 * Description:购物车映射
 */
public interface CartInfoMapper extends BaseMapper<CartInfo> {
    /**
     * 修改某个用户全部购物车商品选择状态
     * @param username
     * @param status
     * @return
     */
    @Update("UPDATE cart_info SET is_checked=#{status} WHERE user_id=#{username}")
    int updateAll(@Param("username") String username, @Param("status")Short status);

    /**
     * 修改某个用户单个购物车商品状态
     * @param username
     * @param cartId
     * @param status
     * @return
     */
    @Update("UPDATE cart_info SET is_checked=#{status} WHERE user_id=#{username} AND id=#{cartId}")
    int updateOne(@Param("username")String username, @Param("cartId")Long cartId, @Param("status")Short status);
}
