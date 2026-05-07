package com.stu.helloserver.service.impl;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.stu.helloserver.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * 聊天服务实现类
 * 使用 Spring AI ChatClient 调用 DashScope / Qwen 大模型
 */
@Service
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;

    /**
     * 通过 ChatClient.Builder 构建 ChatClient 实例
     * 设置默认系统提示词和模型参数
     *
     * @param chatClientBuilder Spring AI 自动注入的 ChatClient 构建器
     */
    public ChatServiceImpl(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
            .defaultSystem("你是一名专业、友好、简洁的中文智能助手。"
                + "请根据用户的问题提供准确、有条理的回答。"
                + "如果不确定答案，请如实告知用户而不要编造信息。")
            .defaultOptions(
                DashScopeChatOptions.builder()
                    .withTopP(0.7)
                    .build()
            )
            .build();
    }

    /**
     * 调用大模型进行聊天
     *
     * @param message 用户输入的消息内容
     * @return 大模型回复的文本内容；如果输入为空则直接返回提示信息
     */
    @Override
    public String chat(String message) {
        // 防御性检查：消息不能为空
        if (message == null || message.isBlank()) {
            return "请输入您的问题。";
        }

        return chatClient.prompt(message)
            .call()
            .content();
    }
}
