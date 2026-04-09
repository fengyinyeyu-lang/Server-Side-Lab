package com.stu.helloserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stu.helloserver.common.Result;
import com.stu.helloserver.dto.UserDTO;
import com.stu.helloserver.entity.User;
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
     * 3. 根据 ID 查询用户 - 路径为 GET /api/users/{id}
     * 使用 @PathVariable 获取 URL 中的路径参数。
     *
     * @param id 用户 ID
     * @return 查询结果（从数据库真实读取）
     */
    @GetMapping("/{id}")
    public Result<String> getUserById(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    /**
     * 4. 获取用户分页列表 - 路径为 GET /api/users/page
     *
     * @param pageNum  当前页码（默认为 1）
     * @param pageSize 每页条数（默认为 5）
     * @return 分页结果数据
     */
    @GetMapping("/page")
    public Result<Page<User>> getUserPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize) {
        return userService.getUserPage(pageNum, pageSize);
    }
}
