package com.stu.helloserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stu.helloserver.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper 接口
 * 继承 MyBatis-Plus 的 BaseMapper，自动获得单表增删改查能力，无需手写基础 SQL。
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // BaseMapper 已包含 insert、selectById、selectList、updateById、deleteById 等方法
    // 如需自定义 SQL，可在此接口中声明方法，并在对应的 XML 文件中编写 SQL
}
