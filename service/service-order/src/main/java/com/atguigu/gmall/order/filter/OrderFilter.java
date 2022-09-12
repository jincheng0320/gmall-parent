package com.atguigu.gmall.order.filter;

import com.atguigu.gmall.order.util.OrderThreadLocalUtil;
import com.atguigu.gmall.order.util.TokenUtil;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * Date:2022/8/10
 * Author:cjc
 * Description:购物车微服务过滤器
 */
@Component
public class OrderFilter extends GenericFilter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //类型转换
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        //获取令牌
        String token = request.getHeader("Authorization").replace("bearer ", "");
        //获取载荷
        Map<String, String> map = TokenUtil.dcodeToken(token);
        if (!map.isEmpty()){
            //获取用户登录名
            String username = map.get("username");
            //存储用户登录名到本地线程工具
            OrderThreadLocalUtil.set(username);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
