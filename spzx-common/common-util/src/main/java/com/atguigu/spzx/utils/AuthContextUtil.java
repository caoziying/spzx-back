package com.atguigu.spzx.utils;

import com.atguigu.spzx.model.entity.system.SysUser;

public class AuthContextUtil {
    public static final ThreadLocal<SysUser> threadLocal = new ThreadLocal<>();

    public static void set(SysUser sysUser) {
        threadLocal.set(sysUser);
    }

    public static SysUser get() {
        return threadLocal.get();
    }

    public static void remove() {
        threadLocal.remove();
    }
}
