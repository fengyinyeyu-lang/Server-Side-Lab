package com.stu.helloserver.controller;

import com.stu.helloserver.common.Result;
import com.stu.helloserver.dto.UserDTO;
import com.stu.helloserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户核心业务接口
 * 所有返回值均通过 Result<T> 统一包装。
 * 通过依赖注入 UserService 接口，实现 Controller 与 Service 的解耦。
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 1. 新增用户（注册） - 路径为 POST /api/users
     *
     * @param userDTO 前端传递的注册用户信息
     * @return 注册结果
     */
    @PostMapping
    public Result<String> register(@RequestBody UserDTO userDTO) {
        return userService.register(userDTO);
    }

    /**
     * 2. 用户登录 - 路径为 POST /api/users/login
     *
     * @param userDTO 前端传递的登录用户信息
     * @return 登录结果（包含 Token）
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody UserDTO userDTO) {
        return userService.login(userDTO);
    }

    /**
     * 3. 获取用户信息（查） - 用于测试拦截器放行
     *
     * @param id 用户 ID
     * @return 查询结果
     */
    @GetMapping("/{id}")
    public Result<String> getUser(@PathVariable("id") Long id) {
        if (id == null || id <= 0) {
            return Result.error("用户 ID 不合法");
        }
        return Result.success("查询成功，正在返回 ID 为 " + id + " 的用户信息");
    }
}
