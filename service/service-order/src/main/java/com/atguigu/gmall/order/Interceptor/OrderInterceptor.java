package com.atguigu.gmall.order.Interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * Date:2022/8/10
 * Author:cjc
 * Description:订单自定义拦截器
 */
@Component
public class OrderInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        //获取主线程request对象
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes != null){
            //获取请求体
            HttpServletRequest request = servletRequestAttributes.getRequest();
            //将原有请求头参数拿出来
            Enumeration<String> headers = request.getHeaderNames();
            //遍历放入requestTemplate
            while (headers.hasMoreElements()) {
                //获取请求头参数的名字
                String name = headers.nextElement();
                //获取参数值
                String value = request.getHeader(name);
                //存储
                requestTemplate.header(name,value);
            }
        }
    }
}
