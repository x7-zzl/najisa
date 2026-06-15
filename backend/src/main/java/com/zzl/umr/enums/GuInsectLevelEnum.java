package com.zzl.umr.enums;


import lombok.Getter;

/**
 * @author zhangzl
 * @description 蛊虫转数枚举类
 * @date 2026/01/22 16:51:40
 */
@Getter
public enum GuInsectLevelEnum {

    /**
     * 蛊虫转数枚举
     * 转数代表蛊虫的等级和强度，从一转到九转
     */
    UNKNOWN(0, "不明"),

    FIRST_TURN(1, "一转"),

    SECOND_TURN(2, "二转"),

    THIRD_TURN(3, "三转"),

    FOURTH_TURN(4, "四转"),

    FIFTH_TURN(5, "五转"),

    SIXTH_TURN(6, "六转"),

    SEVENTH_TURN(7, "七转"),

    EIGHTH_TURN(8, "八转"),

    NINTH_TURN(9, "九转");


    private final Integer code;

    private final String name;

    GuInsectLevelEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据code获取枚举
     */
    public static GuInsectLevelEnum getByCode(Integer code) {
        if (code == null) {
            return UNKNOWN;
        }
        for (GuInsectLevelEnum levelEnum : GuInsectLevelEnum.values()) {
            if (levelEnum.getCode().equals(code)) {
                return levelEnum;
            }
        }
        return UNKNOWN;
    }
}
