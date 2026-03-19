package com.stu.helloserver.exception;

import com.stu.helloserver.common.Result;
import com.stu.helloserver.common.ResultCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 捕获 Controller 层未处理的异常，统一返回 Result 格式的 JSON 响应
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        // 开发阶段可以打印异常堆栈到控制台
        e.printStackTrace();

        // 返回统一 JSON 错误结构，使用 ResultCode 枚举获取标准状态码和消息
        return Result.error(ResultCode.ERROR.getCode(),
                ResultCode.ERROR.getMsg() + " - " + e.getMessage());
    }
}
