package com.atguigu.gmall.user.util;

/**
 * Date:2022/8/9
 * Author:cjc
 * Description:本地线程工具类
 */
public class UserTreadLocalUtil {
    //定义变量
    private final static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    //获取本地线程对象
    public static String get(){
        return threadLocal.get();
    }

    //存储
    public static void set(String username){
        threadLocal.set(username);
    }
}
