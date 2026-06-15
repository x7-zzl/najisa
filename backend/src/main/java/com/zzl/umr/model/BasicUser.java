package com.zzl.umr.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhangzl
 * @description 用户信息表(BasicUser)实体类
 * @date 2025/10/14 23:52
 */
@Data
public class BasicUser implements Serializable {

    /**
     * 人员编号
     */
    private String id;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 密码
     */
    private String password;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 身份证号码
     */
    private String idCard;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 性别，男/女
     */
    private String sex;
    /**
     * 手机号
     */
    private String phoneNumber;
    /**
     * 邮箱地址
     */
    private String emailAddress;
    /**
     * 是否为管理员（0否1是0
     */
    private Integer roleId;
    /**
     * 是否禁用（0否1是）
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

    /**
     * 角色名称
     */
    @TableField(exist = false)
    private String roleName;

    /**
     * 禁用情况
     */
    @TableField(exist = false)
    private String isEnabledStatus;


}

