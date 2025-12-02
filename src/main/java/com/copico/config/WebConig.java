package com.copico.config;

import com.copico.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
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
        registry.addInterceptor(loginInterceptor).excludePathPatterns("/user/login",
                "/user/register",
                "/user/emailResetPassword",
                "/user/sendMailCode/**",
                "/swagger-ui/*",
                "/test/**",
                "/api-docs/swagger-config");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
//                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
