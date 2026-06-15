package com.zzl.umr.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 蛊师蛊虫信息表(RelUserGuInsect)实体类
 *
 * @author zhangzl
 * @since 2026-01-22 16:57:05
 */
@Data
public class RelUserGuInsect implements Serializable {
    /**
     * 主键ID
     */
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 蛊虫/蛊屋ID
     */
    private String guInsectId;

    /**
     * 蛊虫类别（0蛊虫，1蛊屋）
     */
    private Integer guType;

    /**
     * 蛊虫状态（0存活，1濒死，2死亡）
     */
    private Integer guStatus;

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
