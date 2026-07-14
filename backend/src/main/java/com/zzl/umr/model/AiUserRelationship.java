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
 * @description AI用户关系表实体类
 */
@Data
@TableName("ai_user_relationship")
public class AiUserRelationship implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
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
     * 关系等级：0陌生人 1朋友 2暧昧 3恋爱 4热恋 5稳定 6同居
     */
    private Integer level;

    /**
     * 亲密度 0-100
     */
    private Integer intimacy;

    /**
     * 专属称呼（如：宝宝/亲爱的）
     */
    private String nickname;

    /**
     * 纪念日（确定关系的日期）
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date anniversary;

    /**
     * 累计聊天次数
     */
    private Integer totalChats;

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
