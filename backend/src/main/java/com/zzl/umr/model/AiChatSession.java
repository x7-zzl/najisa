package com.zzl.umr.model;

import com.baomidou.mybatisplus.annotation.TableField;
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
 * @description AI聊天会话表(AiChatSession)实体类
 */
@Data
@TableName("ai_chat_session")
public class AiChatSession implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 会话ID
     */
    @TableId
    private String id;

    /**
     * 用户ID（关联basic_user.id）
     */
    private String userId;

    /**
     * 角色ID（关联ai_character.id）
     */
    private String characterId;

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
     * 是否活跃（0否 1是）
     */
    private Integer isActive;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 最后活跃时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 角色名
     */
    @TableField(exist = false)
    private String characterName;

    /**
     * 角色头像
     */
    @TableField(exist = false)
    private String characterAvatar;
}
