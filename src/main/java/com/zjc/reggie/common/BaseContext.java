package com.zjc.reggie.common;

import com.zjc.reggie.dto.LoginDTO;

/**
 * 重要！！！ 客户端的每次请求，服务器端都会分配一个线程来处理，不同请求，不同线程处理
 * 一次请求就会贯穿三层架构
 * 使用ThreadLocal可以记录当前线程，也就是当前请求的一些信息，从而在三层架构使用
 */
public class BaseContext {
    private static final ThreadLocal<LoginDTO> tl = new ThreadLocal<>();

    public static void save(LoginDTO employeeDTO){
        tl.set(employeeDTO);
    }

    public static LoginDTO get(){
        return tl.get();
    }

    public static void remove(){
        tl.remove();
    }
}
