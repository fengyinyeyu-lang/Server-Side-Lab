package com.stu.helloserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用启动类
 * 本项目已完成 MyBatis-Plus 与 PostgreSQL 的集成，不再需要排除数据源配置。
 * 启动前请确保本地 PostgreSQL 已安装并配置。
 *
 * @MapperScan 指定 Mapper 接口所在的包路径，Spring 在启动时会自动扫描并注册。
 */
@SpringBootApplication
@MapperScan("com.stu.helloserver.mapper") // 扫描 Mapper 接口
public class HelloServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloServerApplication.class, args);
	}

}
