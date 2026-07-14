package com.zzl.umr.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangzl
 * @date 2026/3/10  16:20
 * @description 用户个人信息 DTO（含财富数据）
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {

    /**
     * 用户ID
     */
    private String id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 性别
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
     * 用户头像
     */
    private String userAvatar;

    /**
     * 等级数值
     */
    private Integer level;

    /**
     * 等级称号（如"三转蛊师"）
     */
    private String levelTitle;

    /**
     * 等级类别（蛊师 / 蛊仙 / 蛊尊 / 至尊）
     */
    private String levelCategory;

    /**
     * 元石
     */
    private Double originStone;

    /**
     * 仙元石
     */
    private Double immortalOriginStone;

    /**
     * 经验值
     */
    private Long experience;
}
