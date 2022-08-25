package com.yyds.gmall.user.util;

/**
 * @ClassName: CartThreadLocalUtil
 * @Author: yyd
 * @Date: 2022/8/9/009
 * @Description: user微服务的本地线程工具类对象
 */
public class UserThreadLocalUtil {
    //定义一个全局变量
    private final static ThreadLocal<String> threadLocal = new ThreadLocal<>();
    
    /**
     * 获取本地线程的对象
     * @return
     */
    public static String get(){
        return threadLocal.get();
    }
    
    /**
     * 存储数据
     * @param username
     */
    public static void set(String username){
        threadLocal.set(username);
    }
}
