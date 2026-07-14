package com.zzl.umr.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author zhangzl
 * @date 2026/7/13  17:35
 * @description 发给 Python AI 服务的聊天请求
 */
@Data
@Builder
public class PythonChatRequest {

    /**
     * 用户消息
     */
    private String message;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 角色ID
     */
    private String characterId;

    /**
     * 会话ID（用于继续对话）
     */
    private String chatId;

    /**
     * 最近聊天记录
     */
    private java.util.List<HistoryMessage> history;

    /**
     * 当前情绪快照
     */
    private java.util.Map<String, Integer> emotion;

    /**
     * 关系状态
     */
    private java.util.Map<String, Object> relationship;

    /**
     * 相关记忆摘要列表
     */
    private java.util.List<String> memories;

    @Data
    @Builder
    public static class HistoryMessage {

        /**
         * 角色（user / assistant）
         */
        private String role;

        /**
         * 消息内容
         */
        private String content;
    }
}
