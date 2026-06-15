package com.zzl.umr.enums;

import lombok.Getter;

/**
 * @author zhangzl
 * @description 文件存储引擎类型枚举
 * @date 2026/01/16 11:20:28
 */
@Getter
public enum FileEngineTypeEnum {

    /**
     * 本地
     */
    LOCAL("LOCAL"),

    /**
     * MINIO
     */
    MINIO("MINIO");

    private final String value;

    FileEngineTypeEnum(String value) {
        this.value = value;
    }
}
