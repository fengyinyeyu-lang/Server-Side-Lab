package com.stu.helloserver.dto;

import lombok.Data;

/**
 * 聊天请求数据传输对象
 * 封装用户发送给聊天接口的消息内容
 */
@Data
public class ChatRequestDTO {

    /** 用户输入的消息内容 */
    private String message;
}
