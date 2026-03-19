package com.stu.helloserver.controller;

import com.stu.helloserver.common.Result;
import com.stu.helloserver.entity.User;
import org.springframework.web.bind.annotation.*;

/**
 * 用户核心业务接口
 * 所有返回值均通过 Result<T> 统一包装
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /**
     * 用户登录接口（模拟）
     * 此接口在 WebConfig 中被排除拦截，无需 Token 即可访问
     *
     * @param user 登录用户信息
     * @return 模拟的登录令牌
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody User user) {
        // 防御性校验：检查用户名是否为空
        if (user == null || user.getName() == null || user.getName().isBlank()) {
            return Result.error("用户名不能为空");
        }
        // 模拟生成 Token
        String token = "mock-token-" + user.getName() + "-" + System.currentTimeMillis();
        return Result.success(token);
    }

    /**
     * 获取用户信息 (查)
     *
     * @param id 用户 ID
     * @return 查询结果
     */
    @GetMapping("/{id}")
    public Result<String> getUser(@PathVariable("id") Long id) {
        if (id == null || id <= 0) {
            return Result.error("用户 ID 不合法");
        }
        return Result.success("查询成功, 正在返回 ID 为" + id + "的用户信息");
    }

    /**
     * 新增用户 (增) - 接收 JSON 格式数据
     *
     * @param user 用户信息
     * @return 操作结果
     */
    @PostMapping
    public Result<String> createUser(@RequestBody User user) {
        if (user == null || user.getName() == null || user.getName().isBlank()) {
            return Result.error("用户名不能为空");
        }
        return Result.success("新增成功, 接收到用户:" + user.getName() + ", 年龄:" + user.getAge());
    }

    /**
     * 全量更新用户信息 (改)
     *
     * @param id   用户 ID
     * @param user 更新后的用户信息
     * @return 操作结果
     */
    @PutMapping("/{id}")
    public Result<String> updateUser(@PathVariable("id") Long id, @RequestBody User user) {
        if (id == null || id <= 0) {
            return Result.error("用户 ID 不合法");
        }
        if (user == null || user.getName() == null || user.getName().isBlank()) {
            return Result.error("用户名不能为空");
        }
        return Result.success("更新成功, ID" + id + "的用户已修改为:" + user.getName());
    }

    /**
     * 删除用户 (删)
     *
     * @param id 用户 ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable("id") Long id) {
        if (id == null || id <= 0) {
            return Result.error("用户 ID 不合法");
        }
        return Result.success("删除成功, 已移除 ID 为" + id + "的用户");
    }

    /**
     * 测试统一响应拦截与异常处理
     * 刻意制造运行时异常，验证全局异常处理器是否生效
     */
    @GetMapping("/error")
    public Result<String> testError() {
        // 刻意制造一个运行时异常 (ArithmeticException: / by zero)
        int i = 1 / 0;
        return Result.success("这段代码不会正常返回");
    }
}
