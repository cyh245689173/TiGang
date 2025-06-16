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
import com.copico.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 用户 前端控制器
 * </p>
 *
 * @author Bia布
 * @since 2025-06-07
 */
@RestController
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
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BizException(ErrorCode.PARAMS_ERROR.getMessage());
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
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
