package com.copico.common.base;


import lombok.Getter;

/**
 * 错误码
 *
 * @author Bia布
 */
@Getter
public enum ErrorCode {

    SUCCESS("ok", ""),
    PARAMS_ERROR("请求参数错误", ""),
    NULL_ERROR("请求数据为空", ""),
    NOT_LOGIN("未登录", ""),
    NO_AUTH("无权限", ""),
    SYSTEM_ERROR("系统内部异常", "");

    /**
     * 状态码信息
     */
    private final String message;

    /**
     * 状态码描述（详情）
     */
    private final String description;

    ErrorCode(String message, String description) {
        this.message = message;
        this.description = description;
    }


}
