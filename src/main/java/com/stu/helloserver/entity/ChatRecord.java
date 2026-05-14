package com.stu.helloserver.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 聊天记录实体
 * 用于保存每一轮会话记录，当前作为缓存对象使用（存储于 Redis）
 * 后续可扩展为数据库持久化对象（增加 id 字段和表映射注解）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRecord {

    /**
     * 会话编号，标识一组连续对话
     */
    private String sessionId;

    /**
     * 用户提出的问题
     */
    private String userMessage;

    /**
     * 大模型返回的回答
     */
    private String assistantMessage;

    /**
     * 记录创建时间
     */
    private LocalDateTime createTime;
}
