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
 * @description AI用户情绪表(AiUserEmotion)实体类
 */
@Data
@TableName("ai_user_emotion")
public class AiUserEmotion implements Serializable {
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
     * 好感度 0-100
     */
    private Integer love;

    /**
     * 信任度 0-100
     */
    private Integer trust;

    /**
     * 依赖度 0-100
     */
    private Integer depend;

    /**
     * 嫉妒 0-100
     */
    private Integer jealousy;

    /**
     * 生气 0-100
     */
    private Integer anger;

    /**
     * 想念 0-100
     */
    private Integer miss;

    /**
     * 焦虑 0-100
     */
    private Integer anxiety;

    /**
     * 害羞 0-100
     */
    private Integer shy;

    /**
     * 期待 0-100
     */
    private Integer expectation;

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
