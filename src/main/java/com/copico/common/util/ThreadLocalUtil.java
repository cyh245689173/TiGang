package com.copico.common.util;

/**
 * @author 陈玉皓
 * @date 2025/6/15 20:58
 * @description: TODO
 */
public class ThreadLocalUtil {
    private static final ThreadLocal THREAD_LOCAL = new ThreadLocal();

    public static <T> T get() {
        return (T) THREAD_LOCAL.get();
    }

    public static void set(Object value) {
        THREAD_LOCAL.set(value);
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }

}
