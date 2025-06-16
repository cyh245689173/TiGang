package com.copico.interceptors;

import com.copico.common.util.JwtUtil;
import com.copico.common.util.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

import static com.copico.common.base.Constant.AUTHORIZATION_TOKEN_KEY;

/**
 * @author 陈玉皓
 * @date 2025/6/15 20:02
 * @description: 登录拦截器
 */
@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //令牌验证
        String token = request.getHeader(AUTHORIZATION_TOKEN_KEY);
        try {
            Map<String, Object> claims = JwtUtil.parseToken(token);
            //把业务数据存储在ThreadLocal中
            ThreadLocalUtil.set(claims);
            //放行
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            log.warn("失败咯, 当前请求url为:{}", request.getRequestURL());
            //禁行
            return false;

        }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ThreadLocalUtil.remove();
    }
}
