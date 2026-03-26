package com.stu.helloserver.dto;

/**
 * 用户数据传输对象
 * 专门用于接收前端传递的 JSON 数据，避免将底层数据库实体类直接暴露给表现层。
 */
public class UserDTO {

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    // ========== 构造方法 ==========

    public UserDTO() {
    }

    public UserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // ========== Getter & Setter ==========

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
