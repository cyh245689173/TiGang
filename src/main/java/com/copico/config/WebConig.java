package com.copico.config;

import com.copico.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author 陈玉皓
 * @date 2025/6/15 20:06
 * @description: Web配置类
 */
@Configuration
public class WebConig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //登录和注册接口不拦截，和swagger相关接口...
        registry.addInterceptor(loginInterceptor).excludePathPatterns("/user/login", "/user/register", "/swagger-ui/*", "/api-docs/swagger-config");
    }
}
