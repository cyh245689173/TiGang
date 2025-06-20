package com.copico.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.copico.common.base.ErrorCode;
import com.copico.common.exception.BizException;
import com.copico.common.util.JwtUtil;
import com.copico.mapper.UserMapper;
import com.copico.model.domain.User;
import com.copico.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;


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
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BizException("用户密码过短, 最低八个字符");
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
