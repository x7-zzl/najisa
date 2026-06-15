package com.zzl.umr.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zhangzl
 * @description 评论信息表(BasicComments)实体类
 * @date 2025/10/14 23:52
 */
@Data
public class BasicComments implements Serializable {

    /**
     * 评论ID
     */
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 文章ID/视频ID
     */
    private String bussinessId;

    /**
     * 父级评论ID
     */
    private String parentId;

    /**
     * 顶级评论ID(区分顶级留言和子留言)
     */
    private String rootCommentId;

    /**
     * 评论内容
     */
    private String comment;

    /**
     * 评论类型（0文章 1视频）
     */
    private Integer commentType;

    /**
     * 评论层级（顶层评论0，回复顶层评论1，其他2）
     */
    private Integer commentLevel;


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

    /**
     * 用户昵称
     */
    @TableField(exist = false)
    private String userName;


    /**
     * 被回复人的昵称（非数据库字段，用于显示“回复@xxx”）
     */
    @TableField(exist = false)
    private String replyToUserName;

    /**
     * 子评论
     */
    @TableField(exist = false)
    private List<BasicComments> children;

}