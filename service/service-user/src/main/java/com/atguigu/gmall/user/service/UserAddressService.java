package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserAddress;

import java.util.List;

/**
 * Date:2022/8/8
 * Author:cjc
 * Description:用户收获地址接口
 */
public interface UserAddressService {

    /**
     * 根据用户名查询收货地址信息
     * @return
     */
    public List<UserAddress> getUserAddress();
}
