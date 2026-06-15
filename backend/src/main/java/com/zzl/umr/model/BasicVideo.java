package com.zzl.umr.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("basic_video")
public class BasicVideo implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private String id;

    private String title;

    private String intro;

    private String authorId;

    private Integer isHot;


    private String videoUrl;

    private String coverUrl;

    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @TableField(exist = false)
    private String authorName;

    @TableField(exist = false)
    private Long likeCount;

    @TableField(exist = false)
    private Long collectCount;

    @TableField(exist = false)
    private Long coinCount;

    @TableField(exist = false)
    private Integer isNew;
}
