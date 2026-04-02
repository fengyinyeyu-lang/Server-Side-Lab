package com.stu.helloserver.service;

import com.stu.helloserver.common.Result;
import com.stu.helloserver.dto.UserDTO;

/**
 * 用户业务逻辑接口
 * 定义注册、登录、查询等核心业务方法。
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param userDTO 前端传递的用户注册数据
     * @return 注册结果
     */
    Result<String> register(UserDTO userDTO);

    /**
     * 用户登录
     *
     * @param userDTO 前端传递的用户登录数据
     * @return 登录结果（包含 Token）
     */
    Result<String> login(UserDTO userDTO);

    /**
     * 根据 ID 查询用户
     *
     * @param id 用户 ID
     * @return 查询结果（返回用户名）
     */
    Result<String> getUserById(Long id);
}
