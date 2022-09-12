package com.atguigu.gmall.oauth.service.impl;

import com.atguigu.gmall.oauth.service.LoginService;
import com.atguigu.gmall.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.naming.ldap.PagedResultsControl;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

/**
 * Date:2022/8/8
 * Author:cjc
 * Description:用户登录接口实现类
 */
@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private RestTemplate restTemplate;

    @Resource
    private LoadBalancerClient loadBalancerClient;
    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public AuthToken login(String username, String password) {
        //参数校验
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            throw new RuntimeException("用户名和密码不能为空");
        }
        //url地址
        ServiceInstance choose = loadBalancerClient.choose("service-oauth");
        String url = choose.getUri().toString()+"/oauth/token";
        //初始化请求头
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.set("Authorization", getHeadValue());
        //初始化请求体
        MultiValueMap<String, String> body = new HttpHeaders();
        body.set("username", username);
        body.set("password", password);
        body.set("grant_type", "password");
        //构造参数
        HttpEntity<Object> httpEntity = new HttpEntity<>(body, headers);
        /*
          发请求
          1.请求路径
          2.请求方式
          3.请求参数
          4.返回结果类型
         */
        ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
        //获取结果
        Map<String,String> result = exchange.getBody();
        //包装令牌信息
        AuthToken token = new AuthToken();
        token.setJti(result.get("jti"));
        token.setAccessToken(result.get("access_token"));
        token.setRefreshToken(result.get("refresh_token"));
        //返回
        return token;
    }

    @Value("${auth.clientId}")
    private String clientId;
    @Value("${auth.clientSecret}")
    private String clientSecret;

    /**
     * 拼接请求头参数
     */
    private String getHeadValue(){
        //拼接明文
        String head = clientId+":"+clientSecret;
        //加密
        byte[] encode = Base64.getEncoder().encode(head.getBytes());
        //返回
        return "Basic "+new String(encode);
    }

}
