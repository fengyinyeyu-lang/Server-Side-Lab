package com.stu.helloserver.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 聊天响应视图对象
 * 封装返回给前端的聊天问答结果，包含原始问题与模型回答
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseVO {

    /** 用户原始提问内容 */
    private String question;

    /** 大模型返回的回答内容 */
    private String answer;
}
