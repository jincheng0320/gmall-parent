package com.atguigu.gmall.seckill.filter;

import com.atguigu.gmall.seckill.util.SeckillThreadLocalUtil;
import com.atguigu.gmall.seckill.util.TokenUtil;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * Date:2022/8/9
 * Author:cjc
 * Description:购物车微服务过滤器
 */
@WebFilter(filterName = "CartFilter", urlPatterns = "/*")
@Order(value = 1)
public class CartFilter extends GenericFilter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //request类型转换
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        //获取令牌
        String token = request.getHeader("Authorization").replace("bearer ", "");
        //获取载荷
        Map<String, String> map = TokenUtil.dcodeToken(token);
        if (!map.isEmpty()){
            //获取用户名
            String username = map.get("username");
            //存储到本地线程
            SeckillThreadLocalUtil.set(username);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
