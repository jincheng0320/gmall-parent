package com.atguigu.gmall.user.filter;

import com.atguigu.gmall.user.util.TokenUtil;
import com.atguigu.gmall.user.util.UserTreadLocalUtil;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * Date:2022/8/9
 * Author:cjc
 * Description:
 */
@WebFilter(filterName = "UserFilter",urlPatterns = "/*")
@Order(value = 1)
public class UserFilter extends GenericFilter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //类型转换
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        //获取token
        String token = request.getHeader("Authorization").replace("bearer ", "");
        //获取载荷
        Map<String, String> map = TokenUtil.dcodeToken(token);
        if (!map.isEmpty()){
            String username = map.get("username");
            //储存到本地线程对象
            UserTreadLocalUtil.set(username);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
