package com.zzl.umr.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangzl
 * @description 用户个人信息 DTO（含财富数据）
 * @date 2026/03/10 16:20:03
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {
    // ====== 基本信息 ======
    private String id;
    private String userName;
    private String nickName;
    private String idCard;
    private Integer age;
    private String sex;
    private String phoneNumber;
    private String emailAddress;
    private String userAvatar;

    // ====== 财富信息（只读） ======
    /** 等级数值 */
    private Integer level;
    /** 等级称号（如"三转蛊师"） */
    private String levelTitle;
    /** 等级类别（蛊师/蛊仙/蛊尊/至尊） */
    private String levelCategory;
    /** 元石 */
    private Double originStone;
    /** 仙元石 */
    private Double immortalOriginStone;
    /** 经验值 */
    private Long experience;
}
