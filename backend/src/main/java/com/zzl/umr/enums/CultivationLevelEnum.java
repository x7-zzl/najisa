package com.zzl.umr.enums;

import lombok.Getter;


/**
 * @author zhangzl
 * @description 境界枚举类
 * @date 2026/07/01 16:21:28
 */
@Getter
public enum CultivationLevelEnum {

    UNKNOWN(0, "无", ""),

    FIRST(1, "一转蛊师", "蛊师"),

    SECOND(2, "二转蛊师", "蛊师"),

    THIRD(3, "三转蛊师", "蛊师"),

    FOURTH(4, "四转蛊师", "蛊师"),

    FIFTH(5, "五转蛊师", "蛊师"),

    SIXTH(6, "六转蛊仙", "蛊仙"),

    SEVENTH(7, "七转蛊仙", "蛊仙"),

    EIGHTH(8, "八转蛊仙", "蛊仙"),

    NINTH(9, "九转蛊尊", "蛊尊"),

    TENTH(10, "十转至尊", "至尊");

    private final Integer code;
    private final String title;
    private final String category;

    CultivationLevelEnum(Integer code, String title, String category) {
        this.code = code;
        this.title = title;
        this.category = category;
    }

    /**
     * 根据等级 code 获取对应境界枚举
     */
    public static CultivationLevelEnum getByCode(Integer code) {
        if (code == null || code < 1 || code > 10) {
            return UNKNOWN;
        }
        for (CultivationLevelEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return UNKNOWN;
    }
}
