package com.stu.helloserver.service;

/**
 * 聊天服务接口
 * 定义与大模型交互的聊天方法
 */
public interface ChatService {

    /**
     * 向大模型发送消息并获取回复
     *
     * @param message 用户输入的消息内容，不能为空
     * @return 大模型返回的回答文本
     */
    String chat(String message);
}
