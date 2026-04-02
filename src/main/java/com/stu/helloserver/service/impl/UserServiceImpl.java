package com.stu.helloserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stu.helloserver.common.Result;
import com.stu.helloserver.common.ResultCode;
import com.stu.helloserver.dto.UserDTO;
import com.stu.helloserver.entity.User;
import com.stu.helloserver.mapper.UserMapper;
import com.stu.helloserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 用户业务逻辑实现类
 * 本版本已接入 MyBatis-Plus 持久化，直接操作 PostgreSQL 数据库。
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper; // 注入真正的数据库访问层

    @Override
    public Result<String> register(UserDTO userDTO) {
        // 1. 简易防御校验
        if (userDTO == null || userDTO.getUsername() == null || userDTO.getUsername().isBlank()) {
            return Result.error("用户名不能为空");
        }
        if (userDTO.getPassword() == null || userDTO.getPassword().isBlank()) {
            return Result.error("密码不能为空");
        }

        // 2. 真实数据库查询：使用 LambdaQueryWrapper 校验用户是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDTO.getUsername());
        User existingUser = userMapper.selectOne(queryWrapper);

        if (existingUser != null) {
            return Result.error(ResultCode.USER_HAS_EXISTED);
        }

        // 3. 构建实体类并保存到数据库
        User newUser = new User();
        newUser.setUsername(userDTO.getUsername());
        newUser.setPassword(userDTO.getPassword()); // 实际项目通常会对密码进行加密(如 BCrypt)

        int rows = userMapper.insert(newUser);
        if (rows > 0) {
            return Result.success("注册成功");
        }
        return Result.error("注册失败，请稍后再试");
    }

    @Override
    public Result<String> login(UserDTO userDTO) {
        // 1. 简易防御校验
        if (userDTO == null || userDTO.getUsername() == null || userDTO.getUsername().isBlank()) {
            return Result.error("用户名不能为空");
        }
        if (userDTO.getPassword() == null || userDTO.getPassword().isBlank()) {
            return Result.error("密码不能为空");
        }

        // 2. 真实数据库查询：根据用户名查询记录
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDTO.getUsername());
        User user = userMapper.selectOne(queryWrapper);

        // 3. 结果校验
        if (user == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }
        if (!user.getPassword().equals(userDTO.getPassword())) {
            return Result.error(ResultCode.PASSWORD_ERROR);
        }

        // 4. 生成 Token 并返回
        String token = "Bearer " + UUID.randomUUID().toString();
        return Result.success(token);
    }

    @Override
    public Result<String> getUserById(Long id) {
        // 1. 防御校验
        if (id == null || id <= 0) {
            return Result.error("用户 ID 不合法");
        }

        // 2. 使用 MyBatis-Plus 内置的 selectById 方法查询
        User user = userMapper.selectById(id);

        // 3. 结果校验
        if (user == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }

        return Result.success("查询成功，用户名：" + user.getUsername());
    }
}
