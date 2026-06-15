package com.zzl.umr.enums;

import lombok.Getter;

/**
 * @author zhangzl
 * @description 账号禁用枚举类
 * @date 2026/01/27 15:32:20
 */
@Getter
public enum UserDisEnabledEnum {

    ENABLED(0, "正常"),

    DISABLED(1, "禁用")

    ;

    private final Integer code;

    private final String name;

    UserDisEnabledEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
