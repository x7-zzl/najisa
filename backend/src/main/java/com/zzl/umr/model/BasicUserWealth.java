package com.zzl.umr.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户财富信息表(BasicUserWealth)实体类
 *
 * @author zhangzl
 * @since 2026-01-16 10:41:45
 */
@Data
public class BasicUserWealth implements Serializable {
    /**
     * id
     */
    private String id;

    /**
     * 用户表主键id
     */
    private String userId;

    /**
     * 元石
     */
    private Double originStone;

    /**
     * 仙元石
     */
    private Double immortalOriginStone;

    /**
     * 等级
     */
    private Integer level;

    /**
     * 经验值
     */
    private Long experience;

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
