package com.copico.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.copico.common.base.ErrorCode;
import com.copico.common.base.RestResult;
import com.copico.common.exception.BizException;
import com.copico.model.domain.User;
import com.copico.model.request.UserLoginRequest;
import com.copico.model.request.UserRegisterRequest;
import com.copico.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.copico.common.base.UserConstant.ADMIN_ROLE;
import static com.copico.common.base.UserConstant.USER_LOGIN_STATE;

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

    @Autowired
    private UserServiceImpl userService;

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
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return RestResult.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public RestResult<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return RestResult.fail(ErrorCode.PARAMS_ERROR.getMessage());
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return RestResult.fail(ErrorCode.PARAMS_ERROR.getMessage());
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return RestResult.success(user);
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
     * 获取当前用户
     *
     * @param request
     * @return
     */
    @GetMapping("/current")
    @Operation(summary = "获取当前用户")
    public RestResult<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BizException(ErrorCode.NOT_LOGIN.getMessage());
        }
        long userId = currentUser.getId();
        // TODO 校验用户是否合法
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return RestResult.success(safetyUser);
    }


    @GetMapping("/search")
    @Operation(summary = "查询用户")
    public RestResult<List<User>> searchUsers(String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BizException(ErrorCode.NO_AUTH.getMessage());
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("user_name", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return RestResult.success(list);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除用户")
    public RestResult<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BizException(ErrorCode.NO_AUTH.getMessage());
        }
        if (id <= 0) {
            throw new BizException(ErrorCode.PARAMS_ERROR.getMessage());
        }
        boolean b = userService.removeById(id);
        return RestResult.success(b);
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }
}
