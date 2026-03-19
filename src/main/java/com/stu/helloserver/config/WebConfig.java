package com.stu.helloserver.config;

import com.stu.helloserver.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 挂载自定义拦截器，配置拦截与放行规则。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
                // 拦截所有 /api/** 路径下的请求
                .addPathPatterns("/api/**")
                // 只放行登录接口，其余路径交由拦截器内部做精细化判断
                .excludePathPatterns("/api/users/login");
    }
}
