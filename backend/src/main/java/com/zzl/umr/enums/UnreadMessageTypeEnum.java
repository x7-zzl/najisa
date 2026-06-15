package com.zzl.umr.enums;

import lombok.Getter;

/**
 * @author zhangzl
 * @description 未读消息类型枚举类
 * @date 2026/01/30 15:15:37
 */
@Getter
public enum UnreadMessageTypeEnum {

    UN_RADE(0, "未读"),

    BEEN_READ(1, "已读")

    ;

    private final Integer code;

    private final String name;

    UnreadMessageTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据code获取枚举
     */
    public static UnreadMessageTypeEnum getByCode(Integer code) {
        if (code == null) {
            return UN_RADE;
        }
        for (UnreadMessageTypeEnum messageTypeEnum : UnreadMessageTypeEnum.values()) {
            if (messageTypeEnum.getCode().equals(code)) {
                return messageTypeEnum;
            }
        }
        return UN_RADE;
    }
}
