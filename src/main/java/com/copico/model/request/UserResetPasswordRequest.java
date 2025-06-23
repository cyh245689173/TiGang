package com.copico.model.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户重置密码请求类
 */
@Data
public class UserResetPasswordRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 旧密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;
}