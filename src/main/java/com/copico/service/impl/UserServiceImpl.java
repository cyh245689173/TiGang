package com.copico.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.copico.common.base.ErrorCode;
import com.copico.common.exception.BizException;
import com.copico.common.util.JwtUtil;
import com.copico.common.util.ThreadLocalUtil;
import com.copico.config.EmailConfig;
import com.copico.mapper.UserMapper;
import com.copico.model.domain.User;
import com.copico.model.enums.UserRankEnum;
import com.copico.model.request.PasswordResetRequest;
import com.copico.model.request.UserUpdateInfoRequest;
import com.copico.model.response.MailCodeResponse;
import com.copico.service.IUserService;
import com.copico.service.email.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.copico.common.base.UserConstant.USER_LOGIN_STATE;

/**
 * <p>
 * 用户 服务实现类
 * </p>
 *
 * @author Bia布
 * @since 2025-06-07
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final UserMapper userMapper;
    private final EmailService emailService;
    private final EmailConfig emailConfig;
    private final RedisTemplate<String, String> redisTemplate;


    @Value("${file.upload-dir}")
    private String uploadDir;
    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "CoCo";

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    @Override
    public long userRegister(String userAccount, String userName, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userName, userPassword, checkPassword)) {
            throw new BizException("参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BizException("用户账号过短, 账号最低四个字符.");
        }
        if (userPassword.length() < 6 || checkPassword.length() < 6) {
            throw new BizException("用户密码过短, 最低六个字符");
        }

        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;,\\\\<>/?！￥…（）—【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BizException("账户不能包含特殊字符");
        }
        // 密码和校验密码不同
        if (!userPassword.equals(checkPassword)) {
            throw new BizException("密码和校验密码不同");
        }
        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BizException("用户已存在。");
        }

        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserName(userName);
        user.setUserPassword(encryptPassword);
        // 初始化经验值为 0
        user.setExp(0L);
        // 初始化等级为最低等级
        user.setUserLevel(UserRankEnum.values()[0].getDesc());

        int saveResult = this.baseMapper.insert(user);
        if (saveResult == -1) {
            throw new BizException(ErrorCode.SYSTEM_ERROR.getMessage());
        }
        return user.getId();
    }

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @return JWT 令牌
     */
    @Override
    public String userLogin(String userAccount, String userPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BizException("参数不合法, 请检查后重试.");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        queryWrapper.eq("user_password", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            throw new BizException("用户名不存在或密码错误, 请重新检查。");
        }
        // 3. 记录用户的登录态
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("userAccount", userAccount);
        //将 Long 类型字段以字符串形式传输, 避免json序列化导致Long类型退化为Integer
        claims.put("userIdStr", user.getId().toString());
        claims.put("userName", user.getUserName());
        return JwtUtil.genToken(claims);
    }


    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 用户重置密码
     *
     * @param userId      用户 ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 重置是否成功
     */
    @Override
    public boolean resetPassword(Long userId, String oldPassword, String newPassword) {
        if (userId == null || StringUtils.isAnyBlank(oldPassword, newPassword)) {
            throw new BizException(ErrorCode.PARAMS_ERROR.getMessage());
        }
        // 获取用户信息
        User user = this.getById(userId);
        if (user == null) {
            throw new BizException("未获取到获取用户信息");
        }
        // 校验旧密码
        String encryptOldPassword = getEncryptPassword(oldPassword);
        if (!encryptOldPassword.equals(user.getUserPassword())) {
            throw new BizException(ErrorCode.PARAMS_ERROR.getMessage(), "旧密码错误");
        }
        // 加密新密码
        String encryptNewPassword = getEncryptPassword(newPassword);
        user.setUserPassword(encryptNewPassword);
        // 更新用户信息
        return this.updateById(user);
    }

    @Override
    public void uploadAvatar(MultipartFile file) {

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        // 增加空值检查，避免 NPE
        if (originalFilename == null) {
            throw new BizException("文件名称为空");
        }
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFileName = UUID.randomUUID() + fileExtension;

        // 保存文件到服务器
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            // 检查目录创建结果
            boolean mkdirsSuccess = uploadPath.mkdirs();
            if (!mkdirsSuccess) {
                throw new BizException("创建上传目录失败");
            }
        }

        File dest = new File(uploadPath.getAbsolutePath(), uniqueFileName);
        try {
            file.transferTo(dest);
            // 获取当前用户信息
            Map<String, Object> map = ThreadLocalUtil.get();
            Long userId = Long.parseLong((String) map.get("userIdStr"));
            User user = this.getById(userId);
            // 将头像 url 设置为服务器上存储的绝对路径
            user.setAvatarUrl(dest.getAbsolutePath());
            this.updateById(user);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BizException("上传失败");
        }
    }

    @Override
    public boolean updateUserInfo(Long userId, UserUpdateInfoRequest userUpdateInfoRequest) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        if (userUpdateInfoRequest.getUserName() != null) {
            user.setUserName(userUpdateInfoRequest.getUserName());
        }
        if (userUpdateInfoRequest.getGender() != null) {
            user.setGender(userUpdateInfoRequest.getGender());
        }
        if (userUpdateInfoRequest.getPhone() != null) {
            user.setPhone(userUpdateInfoRequest.getPhone());
        }
        if (userUpdateInfoRequest.getEmail() != null) {
            user.setEmail(userUpdateInfoRequest.getEmail());
        }
        if (userUpdateInfoRequest.getUserProfile() != null) {
            user.setUserProfile(userUpdateInfoRequest.getUserProfile());
        }
        return this.updateById(user);
    }

    @Override
    public MailCodeResponse generateMailCode(String email, String type) {
        User user = userMapper.selectOne(Wrappers.lambdaQuery(User.class).eq(User::getEmail, email));
        if ("REGISTER".equalsIgnoreCase(type)) {
            // 如果已经注册的邮箱不发验证码
            if (user != null) {
                throw new BizException("该用户已注册");
            }
        } else {
            if (user == null) {
                throw new BizException("该邮箱未被注册");
            }
        }

        // 生成验证码
        String code = emailService.generateComplexCode(emailConfig.getCodeCount());
        // 验证码唯一 ID, 重置密码时用于验证有效性
        String captchaId = UUID.randomUUID().toString();
        // 存入 Redis
        redisTemplate.opsForValue().set(
                email + emailConfig.getKeyHead() + captchaId,
                code,
                Duration.ofSeconds(emailConfig.getExpireSeconds())
        );
        emailService.sendVerifyCode(email, code);
        // 返回 Code ID
        return new MailCodeResponse(captchaId);
    }

    @Override
    public void resetPasswordByEmail(PasswordResetRequest params) {

        if (!Objects.equals(params.getPassword(), params.getConfirmPassword())) {
            throw new BizException("确认密码与设置的密码不一致");
        }

        String email = params.getEmail();
        String captchaId = email + emailConfig.getKeyHead() + params.getCaptchaId();

        String rdsCode = redisTemplate.opsForValue().get(captchaId);
        String code = params.getCode();

        // 校验验证码
        if (rdsCode == null || !rdsCode.equalsIgnoreCase(code)) {
            throw new BizException("验证码错误");
        }

        User user = userMapper.selectOne(Wrappers.lambdaQuery(User.class).eq(User::getEmail, email));
        if (user == null) {
            throw new BizException("用户不存在");
        }

        String newPassword = getEncryptPassword(params.getPassword());
        userMapper.update(null, Wrappers.lambdaUpdate(User.class)
                .set(User::getUserPassword, newPassword)
                .eq(User::getId, user.getId())
        );

        // 清除当前 TOKEN
        redisTemplate.delete(user.getId() + "");
        redisTemplate.delete(captchaId);
    }

    /**
     * 获取加密后的密码
     *
     * @param userPassword 用户密码
     * @return 加密后的密码
     */
    @Override
    public String getEncryptPassword(String userPassword) {
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

}
