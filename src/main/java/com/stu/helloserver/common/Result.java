package com.stu.helloserver.common;

/**
 * 泛型统一响应体封装
 * 作为返回给前端的数据外壳，所有 Controller 的返回值均使用此结构。
 *
 * @param <T> data 字段的类型
 */
public class Result<T> {

    /** 业务状态码 */
    private Integer code;

    /** 提示信息 */
    private String msg;

    /** 响应数据载荷 */
    private T data;

    // ========== 构造方法 ==========

    public Result() {
    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // ========== 静态工厂方法 ==========

    /**
     * 操作成功，携带数据
     *
     * @param data 响应数据
     * @return 成功的 Result 实例
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), data);
    }

    /**
     * 操作成功，不携带数据
     *
     * @return 成功的 Result 实例
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), null);
    }

    /**
     * 操作失败，使用预定义的 ResultCode
     *
     * @param resultCode 预定义的错误状态码枚举
     * @return 失败的 Result 实例
     */
    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMsg(), null);
    }

    /**
     * 操作失败，自定义状态码和消息（供全局异常处理等场景使用）
     *
     * @param code 状态码
     * @param msg  错误消息
     * @return 失败的 Result 实例
     */
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    /**
     * 操作失败，仅自定义消息（默认使用 500 状态码）
     *
     * @param msg 错误消息
     * @return 失败的 Result 实例
     */
    public static <T> Result<T> error(String msg) {
        return new Result<>(ResultCode.ERROR.getCode(), msg, null);
    }

    // ========== Getter & Setter ==========

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
