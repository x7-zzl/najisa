package com.zzl.umr.enums;

import lombok.Getter;

/**
 * @author zhangzl
 * @description 蛊屋类型
 * @date 2026/01/22 16:54:09
 */
@Getter
public enum GuHouseTypeEnum {
    UNKNOWN(0, "凡蛊屋"),

    REFINING_DAO(1, "仙蛊屋")

    ;

    private final Integer code;

    private final String name;

    GuHouseTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
