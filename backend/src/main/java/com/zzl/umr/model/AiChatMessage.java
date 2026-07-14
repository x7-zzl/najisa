package com.zzl.umr.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author zhangzl
 * @date 2026/7/13  17:35
 * @description AI聊天消息表(AiChatMessage)实体类
 */
@Data
@TableName("ai_chat_message")
public class AiChatMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    @TableId
    private String id;

    /**
     * 会话ID（关联ai_chat_session.id）
     */
    private String sessionId;

    /**
     * 角色：user/assistant/system
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * Token消耗数
     */
    private Integer tokenCount;

    /**
     * 发送时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
