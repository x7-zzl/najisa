package com.zzl.umr.model.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author zhangzl
 * @date 2026/7/13  17:35
 * @description Python AI 服务的聊天响应
 */
@Data
public class PythonChatResponse {

    /**
     * AI 回复文本
     */
    private String reply;

    /**
     * 情绪快照
     */
    private Map<String, Integer> emotion;

    /**
     * 新提取的记忆
     */
    private List<String> memory;

    /**
     * Token 用量
     */
    private Map<String, Integer> usage;
}
