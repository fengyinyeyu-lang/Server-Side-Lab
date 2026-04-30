package com.stu.helloserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stu.helloserver.common.Result;
import com.stu.helloserver.common.ResultCode;
import com.stu.helloserver.dto.UserDTO;
import com.stu.helloserver.entity.User;
import com.stu.helloserver.mapper.UserMapper;
import com.stu.helloserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;
import java.util.UUID;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.stu.helloserver.entity.UserInfo;
import com.stu.helloserver.mapper.UserInfoMapper;
import com.stu.helloserver.vo.UserDetailVO;
import com.stu.helloserver.security.JwtUtil;

/**
 * 用户业务逻辑实现类
 * 本版本已接入 MyBatis-Plus 持久化，直接操作 PostgreSQL 数据库。
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper; // 注入真正的数据库访问层

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String CACHE_KEY_PREFIX = "user:detail:";

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
        String jwt = jwtUtil.generateToken(userDTO.getUsername());
        return Result.success(jwt);
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

    @Override
    public Result<Page<User>> getUserPage(Integer pageNum, Integer pageSize) {
        // 1. 创建 MyBatis-Plus 的 Page 对象
        Page<User> page = new Page<>(pageNum, pageSize);

        // 2. 调用 userMapper 全新提供的方法，条件传 null 代表无条件分页全查
        Page<User> userPage = userMapper.selectPage(page, null);

        // 3. 将分页查询结果包装成统一返回对象
        return Result.success(userPage);
    }

    @Override
    public Result<UserDetailVO> getUserDetail(Long userId) {
        String key = CACHE_KEY_PREFIX + userId;
        // 1. 先查缓存
        String json = redisTemplate.opsForValue().get(key);
        if (json != null && !json.isBlank()) {
            try {
                UserDetailVO cacheVO = JSONUtil.toBean(json, UserDetailVO.class);
                return Result.success(cacheVO);
            } catch (Exception e) {
                // 缓存数据异常，删掉脏缓存，继续查数据库
                redisTemplate.delete(key);
            }
        }
        // 2. 查数据库
        UserDetailVO detail = userInfoMapper.getUserDetail(userId);
        if (detail == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }
        // 3. 写缓存
        redisTemplate.opsForValue().set(
                key,
                JSONUtil.toJsonStr(detail),
                10,
                TimeUnit.MINUTES
        );
        return Result.success(detail);
    }

    @Override
    @Transactional
    public Result<String> updateUserInfo(UserInfo userInfo) {
        // 参数校验，userInfo不能为空，并且 userId不能为空，后面删除Redis缓存时用
        if (userInfo == null || userInfo.getUserId() == null) {
            return Result.error("参数错误");
        }
        // 先尝试更新数据库
        LambdaUpdateWrapper<UserInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserInfo::getUserId, userInfo.getUserId());
        int updatedCount = userInfoMapper.update(userInfo, updateWrapper);
        if (updatedCount == 0) {
            // 如果查不到记录说明是第一次保存扩展信息，执行插入
            userInfoMapper.insert(userInfo);
        }
        // 删除缓存
        String key = CACHE_KEY_PREFIX + userInfo.getUserId();
        redisTemplate.delete(key);
        return Result.success("更新成功");
    }

    @Override
    @Transactional
    public Result<String> deleteUser(Long userId) {
        if (userId == null) {
            return Result.error("参数错误");
        }
        int rows = userMapper.deleteById(userId);
        if (rows > 0) {
            LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserInfo::getUserId, userId);
            userInfoMapper.delete(wrapper);
            
            String key = CACHE_KEY_PREFIX + userId;
            redisTemplate.delete(key);
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
}
