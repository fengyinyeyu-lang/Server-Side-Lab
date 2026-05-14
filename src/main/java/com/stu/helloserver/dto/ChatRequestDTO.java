package com.stu.helloserver.dto;

import lombok.Data;

/**
 * 聊天请求数据传输对象
 * 封装用户发送给聊天接口的消息内容，支持多轮会话
 */
@Data
public class ChatRequestDTO {

    /**
     * 会话编号，用于标识同一轮连续对话
     * 由客户端手动传入（如 "test001"），相同 sessionId 的消息共享上下文
     */
    private String sessionId;

    /** 用户输入的消息内容 */
    private String message;
}
