package com.zzl.umr.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangzl
 * @description
 * @date 2026/03/10 16:20:03
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {
    private String id;
    private String userName;
    private String nickName;
    private String idCard;
    private Integer age;
    private String sex;
    private String phoneNumber;
    private String emailAddress;
    private String userAvatar;
}