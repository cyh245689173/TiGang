package com.copico.model.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.copico.common.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;


/**
 * <p>
 * 用户
 * </p>
 *
 * @author Bia布
 * @since 2025-06-07
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class User extends BaseEntity<User> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户昵称")
    @TableField("user_name")
    private String userName;

    @Schema(description = "账号")
    @TableField("user_account")
    private String userAccount;

    @Schema(description = "用户头像")
    @TableField("avatar_url")
    private String avatarUrl;

    @Schema(description = "性别")
    @TableField("gender")
    private Byte gender;

    @JsonIgnore //让SpringMVC把当前对象转换为json时忽略该字段
    @Schema(description = "密码")
    @TableField("user_password")
    private String userPassword;

    @Schema(description = "电话")
    @TableField("phone")
    private String phone;

    @Schema(description = "邮箱")
    @TableField("email")
    private String email;

    @Schema(description = "等级")
    @TableField("user_rank")
    private String userLevel;

    @Schema(description = "经验值")
    @TableField("user_exp")
    private Long exp;

    @Schema(description = "状态 0 - 正常")
    @TableField("user_status")
    private Integer userStatus;

    @Schema(description = "用户简介")
    @TableField("user_profile")
    private String userProfile;

    @Schema(description = "用户角色 0 - 普通用户 1 - 管理员")
    @TableField("user_role")
    private Integer userRole;

}
