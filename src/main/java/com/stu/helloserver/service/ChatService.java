package com.stu.helloserver.service;

import com.stu.helloserver.dto.ChatRequestDTO;
import com.stu.helloserver.vo.ChatResponseVO;

/**
 * 聊天服务接口
 * 定义与大模型交互的聊天方法，支持多轮会话上下文
 */
public interface ChatService {

    /**
     * 根据请求 DTO 进行聊天，支持会话上下文管理
     *
     * @param requestDTO 包含 sessionId 和 message 的聊天请求
     * @return 封装了问题和回答的响应 VO
     */
    ChatResponseVO chat(ChatRequestDTO requestDTO);
}

