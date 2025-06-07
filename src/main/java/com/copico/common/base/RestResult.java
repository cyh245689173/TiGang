package com.copico.common.base;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 陈玉皓
 * @date 2025/6/4 20:50
 * @description: 响应数据封装类
 */
@Data
@AllArgsConstructor
public class RestResult<T> implements Serializable {
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_FAIL = "fail";
    private String status;
    private String message;
    private T content;

    /**
     * 返回一个成功的数据封装
     *
     * @param message
     * @param content
     * @param <T>
     * @return
     */
    public static <T> RestResult<T> success(String message, T content) {
        return new RestResult<>(STATUS_SUCCESS, message, content);
    }

    public static <T> RestResult<T> success() {
        return success(null, null);
    }

    public static <T> RestResult<T> success(T content) {
        return success(null, content);
    }


    /**
     * 返回一个失败的数据封装
     *
     * @param message
     * @param <T>
     * @return
     */
    public static <T> RestResult<T> fail(String message) {
        return new RestResult<>(STATUS_FAIL, message, null);
    }
}




