package com.zzl.umr.enums;

import lombok.AllArgsConstructor;

/**
 * @author zhangzl
 * @description 标签类型枚举类
 * @date 2025/10/20 1:40
 */
@AllArgsConstructor
public enum TagTypeEnum {

    USER(1, "用户"),

    DEVICE(2, "设备"),

    ;

    private  Integer code;
    private String name;


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
