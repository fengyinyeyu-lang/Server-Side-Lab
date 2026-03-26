package com.stu.helloserver.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stu.helloserver.common.Result;
import com.stu.helloserver.common.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

/**
 * 自定义鉴权拦截器
 * 在请求到达 Controller 前进行 Token 校验，保护接口不被未授权访问。
 * 同时支持基于 HTTP 动词的精细化放行策略（附加题）。
 */
public class AuthInterceptor implements HandlerInterceptor {

    /** 用于将 Result 对象序列化为 JSON 字符串 */
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        String uri = request.getRequestURI();

        // ========== 附加题：基于 HTTP 动词的精细化放行 ==========

        // 注册放行：POST /api/users（精确匹配，不含子路径）
        if ("POST".equalsIgnoreCase(method) && "/api/users".equals(uri)) {
            return true;
        }

        // ========== 其余所有请求（包括 GET/PUT/DELETE）必须携带 Token ==========

        // ========== 严格校验：DELETE、PUT 等敏感操作必须携带 Token ==========

        // 从请求头获取 Authorization 令牌
        String token = request.getHeader("Authorization");

        // Token 缺失或为空白字符串 → 拦截请求
        if (token == null || token.isBlank()) {
            // 设置响应内容类型为 JSON
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK); // HTTP 状态码保持 200

            // 构造 401 错误响应
            Result<?> result = Result.error(ResultCode.TOKEN_INVALID);
            String jsonStr = objectMapper.writeValueAsString(result);

            // 写入响应体
            PrintWriter writer = response.getWriter();
            writer.write(jsonStr);
            writer.flush();
            writer.close();

            return false; // 拦截请求，不再继续执行
        }

        // Token 存在 → 放行
        return true;
    }
}
