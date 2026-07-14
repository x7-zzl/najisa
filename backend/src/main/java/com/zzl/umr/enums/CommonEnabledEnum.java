package com.zzl.umr.enums;

import lombok.Getter;

/**
 * @author zhangzl
 * @date 2026/7/13  17:35
 * @description 是否启用枚举类
 */
@Getter
public enum CommonEnabledEnum {

    DISABLED(0, "否"),

    ENABLED(1, "是")

    ;

    private final Integer code;

    private final String name;

    CommonEnabledEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
