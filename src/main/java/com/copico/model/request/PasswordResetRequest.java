package com.copico.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author xin.yao
 * @since 2025/7/13
 */
@Data
public class PasswordResetRequest {
    @NotBlank(message = "密码必填")
    @Size(min = 6, message = "密码长度至少为6位")
    @Size(max = 32, message = "密码长度大于了32位")
//    @Pattern(
//            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).+$",
//            message = "密码必须包含小写字母、大写字母、数字和特殊符号"
//    )
    private String password;

    @NotBlank(message = "确认密码必填")
    private String confirmPassword;

    @NotBlank(message = "邮箱必填")
    @Email(message = "请输入有效邮箱")
    private String email;

    @Pattern(
            regexp = "^$|^[a-fA-F0-9\\-]{36}$",
            message = "captchaId must be empty or a valid 36-character UUID"
    )
    private String captchaId;

    @NotBlank(message = "验证码必填")
    private String code;
}
