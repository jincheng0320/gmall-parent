package com.atguigu.gmall.order.util;

/**
 * Date:2022/8/9
 * Author:cjc
 * Description:本地线程工具对象
 */
public class OrderThreadLocalUtil {
    //全局变量
    private final static ThreadLocal<String> treadLocal = new ThreadLocal<>();

    /**
     * 获取本地线程对象
     * @return
     */
    public static String get(){
        return treadLocal.get();
    }

    /**
     * 存储对象
     * @param username
     */
    public static void set(String username){
        treadLocal.set(username);
    }
}
