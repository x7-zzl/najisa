package com.zzl.umr.enums;

import lombok.Getter;

/**
 * @author zhangzl
 * @description 消息类型枚举类
 * @date 2026/01/30 16:55:13
 */
@Getter
public enum MessageTypeEnum {
    UNKNOWN(0, "未知"),

    GU_INSECT(1, "蛊虫");

    private final Integer code;

    private final String name;

    MessageTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据code获取枚举
     */
    public static MessageTypeEnum getByCode(Integer code) {
        if (code == null) {
            return UNKNOWN;
        }
        for (MessageTypeEnum messageTypeEnum : MessageTypeEnum.values()) {
            if (messageTypeEnum.getCode().equals(code)) {
                return messageTypeEnum;
            }
        }
        return UNKNOWN;
    }
}
