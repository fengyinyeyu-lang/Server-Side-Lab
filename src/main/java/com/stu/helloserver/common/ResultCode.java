package com.stu.helloserver.common;

/**
 * 全局业务状态码枚举
 * 在前后端分离架构中，HTTP 状态码通常保持 200，
 * 真实的业务结果通过此枚举定义的自定义状态码在 JSON 中传递。
 */
public enum ResultCode {

    /** 通用成功 */
    SUCCESS(200, "操作成功"),

    /** 通用系统错误 */
    ERROR(500, "系统繁忙,请稍后再试"),

    /** 登录凭证缺失或过期 */
    TOKEN_INVALID(401, "登录凭证已缺失或过期,请重新登录");

    /** 业务状态码 */
    private final int code;

    /** 状态码描述信息 */
    private final String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
