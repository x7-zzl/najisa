package com.zzl.umr.enums;

import lombok.Getter;

/**
 * @author zhangzl
 * @description 用户身份枚举类
 * @date 2026/01/27 15:30:51
 */
@Getter
public enum UserRoleEnum {
    USER(0, "普通用户"),
    ADMIN(1, "管理员")

    ;

    private final Integer code;

    private final String name;

    UserRoleEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
