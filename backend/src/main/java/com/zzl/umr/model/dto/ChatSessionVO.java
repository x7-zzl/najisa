package com.zzl.umr.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author zhangzl
 * @date 2026/7/13  17:35
 * @description 聊天会话列表项 VO
 */
@Data
@Builder
public class ChatSessionVO {

    /**
     * 会话ID
     */
    private String chatId;

    /**
     * 角色ID
     */
    private String characterId;

    /**
     * 角色名
     */
    private String characterName;

    /**
     * 角色头像
     */
    private String characterAvatar;

    /**
     * 会话标题
     */
    private String title;

    /**
     * 最后一条消息摘要
     */
    private String lastMessage;

    /**
     * 消息总数
     */
    private Integer messageCount;

    /**
     * 最后活跃时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
