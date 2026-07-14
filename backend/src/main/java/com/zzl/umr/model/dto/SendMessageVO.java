package com.zzl.umr.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @author zhangzl
 * @date 2026/7/13  17:35
 * @description 发送消息后返回给前端的 VO
 */
@Data
@Builder
public class SendMessageVO {

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * AI 回复内容
     */
    private String reply;

    /**
     * 当前情绪快照
     */
    private Map<String, Integer> emotion;

    /**
     * 关系状态
     */
    private Map<String, Object> relationship;

    /**
     * Token 用量
     */
    private Map<String, Integer> usage;
}
