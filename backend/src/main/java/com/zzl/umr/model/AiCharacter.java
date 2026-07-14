package com.zzl.umr.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
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
 * @description AI角色表实体类
 */
@Data
@TableName("ai_character")
public class AiCharacter implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @TableId
    private String id;

    /**
     * 角色名
     */
    private String name;

    /**
     * 昵称/对自己的称呼
     */
    private String nickname;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 性别
     */
    private String gender;

    /**
     * 职业
     */
    private String occupation;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 性格JSON（性格特质、MBTI等）
     */
    private String personalityJson;

    /**
     * 说话风格JSON（口头禅、回复长度、是否发表情等）
     */
    private String speechJson;

    /**
     * 身世背景JSON（家庭、成长经历、童年等）
     */
    private String backgroundJson;

    /**
     * 习惯爱好JSON
     */
    private String habitJson;

    /**
     * 恋爱观JSON
     */
    private String loveJson;

    /**
     * 系统Prompt（生成规则）
     */
    private String systemPrompt;

    /**
     * 是否启用（0否 1是）
     */
    private Integer isEnabled;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
