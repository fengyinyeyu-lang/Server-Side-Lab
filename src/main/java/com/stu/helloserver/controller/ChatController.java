package com.stu.helloserver.controller;

import com.stu.helloserver.common.Result;
import com.stu.helloserver.dto.ChatRequestDTO;
import com.stu.helloserver.service.ChatService;
import com.stu.helloserver.vo.ChatResponseVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 聊天控制器
 * 提供基于 Qwen 大模型的聊天 API 接口，支持多轮会话上下文
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * 聊天接口 - 接收用户消息并返回大模型回复
     * 支持通过 sessionId 维持多轮对话上下文
     *
     * @param requestDTO 包含 sessionId（可选）和用户消息的请求体
     * @return 统一响应体，包含问题和回答
     */
    @PostMapping
    public Result<ChatResponseVO> chat(@RequestBody ChatRequestDTO requestDTO) {
        // 防御性检查：请求体不能为空
        if (requestDTO == null) {
            return Result.error("请求体不能为空");
        }

        // 防御性检查：消息内容不能为空
        if (requestDTO.getMessage() == null || requestDTO.getMessage().isBlank()) {
            return Result.error("消息内容不能为空");
        }

        ChatResponseVO responseVO = chatService.chat(requestDTO);
        return Result.success(responseVO);
    }
}
