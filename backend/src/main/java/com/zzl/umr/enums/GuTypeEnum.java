package com.zzl.umr.enums;

import lombok.Getter;

/**
 * @author zhangzl
 * @description 蛊虫类型
 * @date 2026/01/22 16:49:24
 */
@Getter
public enum GuTypeEnum {

    COMMON(0, "凡蛊"),

    IMMORTAL(1, "仙蛊")

    ;

    private final Integer code;

    private final String name;

    GuTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据code获取枚举
     */
    public static GuTypeEnum getByCode(Integer code) {
        if (code == null) {
            return COMMON;
        }
        for (GuTypeEnum guTypeEnum : GuTypeEnum.values()) {
            if (guTypeEnum.getCode().equals(code)) {
                return guTypeEnum;
            }
        }
        return COMMON;
    }
}
