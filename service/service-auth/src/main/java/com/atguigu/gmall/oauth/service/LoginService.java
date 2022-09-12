package com.atguigu.gmall.oauth.service;

import com.atguigu.gmall.oauth.util.AuthToken;

/**
 * Date:2022/8/8
 * Author:cjc
 * Description:用户登录接口
 */
public interface LoginService {

    public AuthToken login(String username, String password);
}
