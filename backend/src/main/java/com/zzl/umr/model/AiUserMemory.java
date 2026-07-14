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
 * @description AI长期记忆表(AiUserMemory)实体类
 */
@Data
@TableName("ai_user_memory")
public class AiUserMemory implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 记忆ID
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
     * 类型：preference偏好/event事件/fact事实/emotion情绪/promise承诺/habit习惯
     */
    private String type;

    /**
     * 记忆摘要
     */
    private String summary;

    /**
     * 详细内容
     */
    private String detail;

    /**
     * 重要性 1-10（越高越容易被检索）
     */
    private Integer importance;

    /**
     * 被触发/召回次数
     */
    private Integer triggerCount;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 最后更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
