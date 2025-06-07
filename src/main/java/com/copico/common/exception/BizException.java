package com.copico.common.exception;

import lombok.Data;

/**
 * @author 陈玉皓
 * @date 2025/6/7 11:10
 * @description: 全局业务异常类
 */
@Data
public class BizException extends RuntimeException {

    private String code;

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizException(String code, String message) {
        super(message);
        this.setCode(code);
    }

    public BizException(String message) {
        super(message);

    }

    public BizException(Throwable cause) {
        super(cause);
    }

}
