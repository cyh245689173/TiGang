package com.copico.model.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户修改信息请求对象
 */
@Data
public class UserUpdateInfoRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 性别
     */
    private Byte gender;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户简介
     */
    private String userProfile;
}