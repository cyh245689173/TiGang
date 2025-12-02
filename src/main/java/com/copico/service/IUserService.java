package com.copico.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.copico.model.domain.User;
import com.copico.model.request.PasswordResetRequest;
import com.copico.model.request.UserUpdateInfoRequest;
import com.copico.model.response.MailCodeResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 用户 服务类
 * </p>
 *
 * @author Bia布
 * @since 2025-06-07
 */
public interface IUserService extends IService<User> {
    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userName, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @return JWT令牌
     */
    String userLogin(String userAccount, String userPassword);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 获取加密后的密码
     *
     * @param userPassword
     * @return
     */
    String getEncryptPassword(String userPassword);

    boolean resetPassword(Long userId, String oldPassword, String newPassword);

    void uploadAvatar(MultipartFile file);


    /**
     * 修改用户信息
     *
     * @param userId                  用户 ID
     * @param userUpdateInfoRequest   包含要修改信息的请求对象
     * @return 修改是否成功
     */
    boolean updateUserInfo(Long userId, UserUpdateInfoRequest userUpdateInfoRequest);

    MailCodeResponse generateMailCode(String mail, String type);

    void resetPasswordByEmail(PasswordResetRequest params);
}
