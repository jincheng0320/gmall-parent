package com.atguigu.gmall.oauth.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.IpUtil;
import com.atguigu.gmall.oauth.service.LoginService;
import com.atguigu.gmall.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Date:2022/8/8
 * Author:cjc
 * Description:用户登录控制层
 */
@RestController
@RequestMapping("/user/login")
public class LoginController {

    @Autowired
    private LoginService loginService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @GetMapping
    public Result login(String username, String password, HttpServletRequest request){
        //登录
        AuthToken authToken = loginService.login(username, password);
        //将令牌跟ip绑定
        String ipAddress = IpUtil.getIpAddress(request);
        //存入redis
        stringRedisTemplate.opsForValue().set(ipAddress, authToken.getAccessToken());
        return Result.ok(authToken);
    }
}
