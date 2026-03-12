package com.stu.helloserver.exception;

import com.stu.helloserver.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        // 开发阶段可以打印异常堆栈到控制台
        e.printStackTrace();
        
        // 返回统一 JSON 错误结构
        return Result.error(500, "服务器执行异常: " + e.getMessage());
    }
}
