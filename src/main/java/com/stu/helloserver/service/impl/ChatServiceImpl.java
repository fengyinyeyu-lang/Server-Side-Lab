package com.stu.helloserver.service.impl;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.stu.helloserver.dto.ChatRequestDTO;
import com.stu.helloserver.service.ChatService;
import com.stu.helloserver.vo.ChatResponseVO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 聊天服务实现类
 * 使用 Spring AI ChatClient 调用 DashScope / Qwen 大模型
 * 通过 Redis 存储会话上下文，实现多轮对话记忆功能
 */
@Service
public class ChatServiceImpl implements ChatService {

    /** 最大保留的历史对话轮数 */
    private static final int MAX_HISTORY_ROUNDS = 3;

    /** Redis key 前缀 */
    private static final String REDIS_KEY_PREFIX = "chat:session:";

    private final ChatClient chatClient;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 通过 ChatClient.Builder 构建 ChatClient 实例
     * 设置默认系统提示词和模型参数
     *
     * @param chatClientBuilder    Spring AI 自动注入的 ChatClient 构建器
     * @param stringRedisTemplate  Spring Data Redis 自动注入的模板
     */
    public ChatServiceImpl(ChatClient.Builder chatClientBuilder, StringRedisTemplate stringRedisTemplate) {
        this.chatClient = chatClientBuilder
                .defaultSystem("你是一名专业、友好、简洁的中文智能助手，请结合历史上下文回答问题。"
                        + "如果历史对话为空，则直接回答当前问题。"
                        + "如果不确定答案，请如实告知用户而不要编造信息。")
                .defaultOptions(
                        DashScopeChatOptions.builder()
                                .withTopP(0.7)
                                .build())
                .build();
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 聊天方法 —— 支持多轮会话上下文
     * 处理流程：
     * 1. 校验请求参数
     * 2. 根据 sessionId 从 Redis 读取历史聊天记录
     * 3. 将历史上下文与当前问题拼接后发送给模型
     * 4. 将本轮对话记录写回 Redis，保留最近 N 轮
     *
     * @param requestDTO 聊天请求，包含 sessionId 和 message
     * @return 聊天响应，包含用户问题和模型回答
     */
    @Override
    public ChatResponseVO chat(ChatRequestDTO requestDTO) {
        // 防御性检查：消息不能为空
        String message = requestDTO.getMessage();
        if (message == null || message.isBlank()) {
            return new ChatResponseVO(message, "请输入您的问题。");
        }

        String sessionId = requestDTO.getSessionId();
        String finalPrompt;

        // 判断是否存在有效的 sessionId，决定是否拼接历史上下文
        if (sessionId != null && !sessionId.isBlank()) {
            String redisKey = REDIS_KEY_PREFIX + sessionId;

            // 1. 从 Redis 读取该会话的全部历史记录
            List<String> records = stringRedisTemplate.opsForList().range(redisKey, 0, -1);

            // 2. 拼接历史上下文
            String historyText = "";
            if (records != null && !records.isEmpty()) {
                historyText = String.join("\n", records);
            }

            // 3. 构建包含历史上下文的最终提示词
            finalPrompt = """
                    以下是历史对话:
                    %s
                    当前用户问题:
                    %s
                    """.formatted(historyText, message);

            // 4. 调用大模型
            String answer = callModel(finalPrompt);

            // 5. 将本轮对话记录保存到 Redis（以可读文本格式）
            String recordText = "用户:" + message + "\n助手:" + answer;
            stringRedisTemplate.opsForList().rightPush(redisKey, recordText);

            // 6. 只保留最近 MAX_HISTORY_ROUNDS 轮对话，防止上下文过长
            Long size = stringRedisTemplate.opsForList().size(redisKey);
            if (size != null && size > MAX_HISTORY_ROUNDS) {
                stringRedisTemplate.opsForList().trim(redisKey, size - MAX_HISTORY_ROUNDS, size - 1);
            }

            return new ChatResponseVO(message, answer);
        } else {
            // 无 sessionId 时，退化为单轮对话（保持向后兼容）
            String answer = callModel(message);
            return new ChatResponseVO(message, answer);
        }
    }

    /**
     * 调用大模型获取回复
     *
     * @param prompt 完整的提示词（可能包含历史上下文）
     * @return 模型返回的文本内容
     */
    private String callModel(String prompt) {
        try {
            String content = chatClient.prompt(prompt)
                    .call()
                    .content();
            return content != null ? content : "抱歉，模型未返回有效回复，请稍后重试。";
        } catch (Exception e) {
            return "调用大模型时发生异常：" + e.getMessage();
        }
    }
}
