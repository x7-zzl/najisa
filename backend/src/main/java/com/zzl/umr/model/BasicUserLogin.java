package com.zzl.umr.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * (BasicUserLogin)实体类
 *
 * @author zhangzl
 * @since 2026-01-15 20:27:58
 */
@Data
public class BasicUserLogin implements Serializable {
    /**
     * id
     */
    private String id;

    /**
     * 用户表主键id
     */
    private String userId;

    /**
     * 用户头像链接
     */
    private String userAvatar;

    /**
     * 登录ip
     */
    private String logInIp;

    /**
     * 登录地址
     */
    private String logInAddress;

    /**
     * 上次登录时间
     */
    private Date lastLoginTime;

    /**
     * 第一次登录时间
     */
    private Date firstLoginTime;

    /**
     * 登录失败次数
     */
    private Integer logInFailNum;

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
