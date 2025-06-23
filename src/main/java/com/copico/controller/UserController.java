package com.copico.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.copico.common.annotation.AuthCheck;
import com.copico.common.base.ErrorCode;
import com.copico.common.base.RestResult;
import com.copico.common.base.UserConstant;
import com.copico.common.entity.DeleteRequest;
import com.copico.common.exception.BizException;
import com.copico.common.util.ThreadLocalUtil;
import com.copico.model.domain.User;
import com.copico.model.dto.user.UserAddRequest;
import com.copico.model.dto.user.UserUpdateRequest;
import com.copico.model.request.UserLoginRequest;
import com.copico.model.request.UserRegisterRequest;
import com.copico.model.request.UserResetPasswordRequest;
import com.copico.model.request.UserUpdateInfoRequest;
import com.copico.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * 用户 前端控制器
 * </p>
 *
 * @author Bia布
 * @since 2025-06-07
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public RestResult<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        // 校验
        if (userRegisterRequest == null) {
            throw new BizException(ErrorCode.PARAMS_ERROR.getMessage());
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userName = userRegisterRequest.getUserName();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userName, userPassword, checkPassword)) {
            throw new BizException(ErrorCode.PARAMS_ERROR.getMessage());
        }
        long result = userService.userRegister(userAccount, userName, userPassword, checkPassword);
        return RestResult.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public RestResult<String> userLogin(@RequestBody UserLoginRequest userLoginRequest) {
        if (userLoginRequest == null) {
            return RestResult.fail(ErrorCode.PARAMS_ERROR.getMessage());
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return RestResult.fail(ErrorCode.PARAMS_ERROR.getMessage());
        }
        String token = userService.userLogin(userAccount, userPassword);
        return RestResult.success(token);
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    @Operation(summary = "用户注销")
    public RestResult<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BizException(ErrorCode.PARAMS_ERROR.getMessage());
        }
        int result = userService.userLogout(request);
        return RestResult.success(result);
    }

    /**
     * 用户重置密码
     *
     * @param userResetPasswordRequest 包含旧密码和新密码的请求对象
     * @return 操作结果
     */
    @PostMapping("/resetPassword")
    @Operation(summary = "用户重置密码")
    public RestResult<Boolean> resetPassword(@RequestBody UserResetPasswordRequest userResetPasswordRequest) {
        // 校验
        if (userResetPasswordRequest == null) {
            throw new BizException(ErrorCode.PARAMS_ERROR.getMessage());
        }
        String oldPassword = userResetPasswordRequest.getOldPassword();
        String newPassword = userResetPasswordRequest.getNewPassword();
        if (StringUtils.isAnyBlank(oldPassword, newPassword)) {
            throw new BizException(ErrorCode.PARAMS_ERROR.getMessage());
        }
        // 获取当前登录用户 ID
        Map<String, Object> map = ThreadLocalUtil.get();
        Long userId = (Long) map.get("userId");
        if (userId == null) {
            throw new BizException(ErrorCode.NOT_LOGIN.getMessage());
        }
        boolean result = userService.resetPassword(userId, oldPassword, newPassword);
        return RestResult.success(result);
    }


    /**
     * 获取用户个人信息
     *
     * @return 用户个人信息
     */
    @GetMapping("/profile")
    public RestResult<User> getUserInfo() {
        Map<String, Object> map = ThreadLocalUtil.get();
        Long userId = Long.parseLong((String) map.get("userIdStr"));
        User user = userService.getById(userId);
        if (user != null) {
            return RestResult.success(user);
        }
        return RestResult.fail("未找到用户信息");
    }


    /**
     * 上传用户头像
     *
     * @param avatar 上传的图片文件
     * @return 操作结果
     */
    @PostMapping("/changeAvatar")
    public RestResult<String> uploadAvatar(@RequestParam("avatar") MultipartFile avatar) {
        if (avatar.isEmpty()) {
            return RestResult.fail("文件为空");
        }
        userService.uploadAvatar(avatar);
        return RestResult.success("上传成功");

    }

    /**
     * 修改用户信息
     *
     * @param userUpdateInfoRequest 包含要修改信息的请求对象
     * @return 操作结果
     */
    @PostMapping("/updateInfo")
    @Operation(summary = "修改用户信息")
    public RestResult<Boolean> updateUserInfo(@RequestBody UserUpdateInfoRequest userUpdateInfoRequest) {
        if (userUpdateInfoRequest == null) {
            throw new BizException(ErrorCode.PARAMS_ERROR.getMessage());
        }
        // 获取当前登录用户 ID
        Map<String, Object> map = ThreadLocalUtil.get();
        Long userId = Long.parseLong((String) map.get("userIdStr"));
        if (userId == null) {
            throw new BizException(ErrorCode.NOT_LOGIN.getMessage());
        }
        boolean result = userService.updateUserInfo(userId, userUpdateInfoRequest);
        return RestResult.success(result);
    }

    /**
     * 创建用户
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public RestResult<Boolean> addUser(@RequestBody UserAddRequest userAddRequest) {
        User user = new User();
        BeanUtil.copyProperties(userAddRequest, user);
        // 默认密码
        final String DEFAULT_PASSWORD = "12345678";
        String encryptPassword = userService.getEncryptPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encryptPassword);
        // 插入数据库
        return RestResult.success(userService.save(user));
    }

    /**
     * 测试
     */
    @GetMapping("/getTest")
    public RestResult<String> getUserById() {
        Map<String, Object> map = ThreadLocalUtil.get();
        String userAccount = (String) map.get("userAccount");
        return RestResult.success("测试" + userAccount);
    }

    /**
     * 根据 id 获取用户（仅管理员）
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public RestResult<User> getUserById(long id) {
        if (id < 1) {
            throw new BizException("id有误, 请检查");
        }
        return RestResult.success(userService.getById(id));
    }

    /**
     * 删除用户
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public RestResult<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        boolean b = userService.removeById(deleteRequest.getId());
        return RestResult.success(b);
    }

    /**
     * 更新用户
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public RestResult<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        userService.updateById(user);
        return RestResult.success(userService.updateById(user));
    }

}
