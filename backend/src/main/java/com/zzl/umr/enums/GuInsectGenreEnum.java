package com.zzl.umr.enums;

import lombok.Getter;

/**
 * @author zhangzl
 * @description 蛊虫流派枚举类
 * 《蛊真人》流派枚举类
 * @date 2026/01/22 15:07:17
 */
@Getter
public enum GuInsectGenreEnum {
    /**
     * 蛊虫流派枚举
     */
    UNKNOWN(0, "不明"),

    REFINING_DAO(1, "炼道"),

    WISDOM_DAO(2, "智道"),

    RULE_DAO(3, "律道"),

    WIND_DAO(4, "风道"),

    WATER_DAO(5, "水道"),




    FIRE_DAO(6, "火道"),

    STRENGTH_DAO(7, "力道"),

    EARTH_DAO(8, "土道"),

    WOOD_DAO(9, "木道"),

    METAL_DAO(10, "金道"),
    // 时间
    TIME_DAO(11, "宙道"),

    // 空间
    SPACE_DAO(12, "宇道"),

    SOUL_DAO(13, "魂道"),

    TRANSFORMATION_DAO(14, "变化道"),

    ENSLAVEMENT_DAO(15, "奴道"),

    POISON_DAO(16, "毒道"),

    ILLUSION_DAO(17, "幻道"),

    THUNDER_DAO(18, "雷道"),

    ICE_DAO(19, "冰道"),

    HEALING_DAO(20, "治疗道"),

    SEALING_DAO(21, "禁道"),

    DREAM_DAO(22, "梦道"),

    LUCK_DAO(23, "运道"),

    QI_DAO(24, "气道"),

    KILLING_DAO(25, "杀道"),

    FOOD_DAO(26, "食道"),

    THIEF_DAO(27, "偷道"),

    BLOOD_DAO(28, "血道"),

    ARRAY_DAO(29, "阵道"),

    BEAST_DAO(30, "兽道"),

    TRUST_DAO(31, "信道"),

    LIGHT_DAO(32, "光道"),

    DARK_DAO(33, "暗道"),

    VOICE_DAO(34, "音道"),

    SWORD_DAO(35, "剑道"),

    SKY_DAO(36, "天道"),

    HUMAN_DAO(40, "人道"),


    ;

    /**
     * 流派编码
     */
    private final Integer code;

    /**
     * 流派名称
     */
    private final String name;

    GuInsectGenreEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据code获取枚举
     */
    public static GuInsectGenreEnum getByCode(Integer code) {
        if (code == null) {
            return UNKNOWN;
        }
        for (GuInsectGenreEnum genre : GuInsectGenreEnum.values()) {
            if (genre.getCode().equals(code)) {
                return genre;
            }
        }
        return UNKNOWN;
    }


    /**
     * 判断code是否存在
     */
    public static boolean isValidCode(Integer code) {
        if (code == null) {
            return false;
        }
        for (GuInsectGenreEnum genre : GuInsectGenreEnum.values()) {
            if (genre.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }

}