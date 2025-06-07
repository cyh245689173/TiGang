package com.copico.model.domain;

import com.copico.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * <p>
 * 用户
 * </p>
 *
 * @author yuhao.chen
 * @since 2025-06-07
 */
@Data
public class User extends BaseEntity<User> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户昵称")
    private String username;

    @Schema(description = "账号")
    private String userAccount;

    @Schema(description = "用户头像")
    private String avatarUrl;

    @Schema(description = "性别")
    private Byte gender;

    @Schema(description = "密码")
    private String userPassword;

    @Schema(description = "电话")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "状态 0 - 正常")
    private Integer userStatus;

    @Schema(description = "用户角色 0 - 普通用户 1 - 管理员")
    private Integer userRole;

}
