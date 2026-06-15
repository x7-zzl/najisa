package com.zzl.umr.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 蛊虫信息表(BasicGuInsect)实体类
 *
 * @author zhangzl
 * @since 2026-01-22 17:04:01
 */
@Data
public class BasicGuInsect implements Serializable {
    /**
     * id
     */
    private String id;

    /**
     * 蛊虫名称
     */
    private String name;

    /**
     * 蛊虫图片1
     */
    private String guInsectAvatarOne;

    /**
     * 蛊虫图片2
     */
    private String guInsectAvatarTwo;

    /**
     * 蛊虫图片3
     */
    private String guInsectAvatarThree;

    /**
     * 蛊虫描述
     */
    private String description;

    /**
     * 蛊虫类别（0凡蛊，1仙蛊）
     */
    private Integer guType;

    /**
     * 蛊虫流派
     */
    private Integer genre;

    /**
     * 蛊虫转数
     */
    private Integer guLevel;

    /**
     * 库存
     */
    private Integer inventory;

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
     * 蛊虫类别名称（显示用）
     */
    @TableField(exist = false)
    private String guTypeName;

    /**
     * 蛊虫流派名称（显示用）
     */
    @TableField(exist = false)
    private String genreName;

    /**
     * 蛊虫转数名称（显示用）
     */
    @TableField(exist = false)
    private String guLevelName;

}
