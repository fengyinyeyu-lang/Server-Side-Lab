package com.stu.helloserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults()) // 开启全局 CORS 配置
            .csrf(AbstractHttpConfigurer::disable) // 关闭 CSRF 防护，针对前后端分离项目
            .sessionManagement(session -> // 配置 Session 管理策略，设置无状态
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth // 重点：配置接口访问规则
                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()
                .anyRequest().authenticated() // 其他所有请求都必须先认证
            )
            .formLogin(AbstractHttpConfigurer::disable) // 关闭 Spring Security 默认登录页
            .httpBasic(AbstractHttpConfigurer::disable); // 关闭 httpBasic 认证
        
        return http.build();
    }
}
