package com.atguigu.gamll.filter;

import com.atguigu.gamll.config.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Date:2022/8/8
 * Author:cjc
 * Description:自定义全局过滤器
 */
@Component
public class GmallFilter implements GlobalFilter , Ordered {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 自定义过滤器逻辑
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取请求体
        ServerHttpRequest request = exchange.getRequest();
        //获取响应体
        ServerHttpResponse response = exchange.getResponse();
        //从用户请求路径获取token
        String token = request.getQueryParams().getFirst("token");
        //判断请求路径有没有
        if (StringUtils.isEmpty(token)){
            //从head中拿token
            token = request.getHeaders().getFirst("token");
            //判断head中是否有token
            if (StringUtils.isEmpty(token)){
                //从cookie中拿token
                MultiValueMap<String, HttpCookie> cookies = request.getCookies();
                if (cookies != null && cookies.size() > 0){
                    HttpCookie cookie= cookies.getFirst("token");
                    if (cookie != null){
                        token = cookie.getValue();
                    }
                }
            }
        }
        //全部没有 拒绝请求
        if (StringUtils.isEmpty(token)){
            response.setStatusCode(HttpStatus.SEE_OTHER);
            response.setComplete();
        }
        //获取当前ip
        String gatwayIpAddress = IpUtil.getGatwayIpAddress(request);
        //根据id从redis中拿token
        String redisToken = stringRedisTemplate.opsForValue().get(gatwayIpAddress);
        if (StringUtils.isEmpty(redisToken)){
            //用户没有登录
            response.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
            response.setComplete();
        }
        //判断redis是否和用户给的一致
        if (!redisToken.equals(token)){
            response.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
            response.setComplete();
        }
        //将token放入请求头中
        request.mutate().header("Authorization","bearer "+token);
        //放行
        return chain.filter(exchange);
    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
