package com.zzl.umr.enums;

import lombok.Getter;

/**
 * @author zhangzl
 * @description 结果枚举类
 * @date 2025/10/14 23:08
 */

@Getter
public enum ResultCodeEnum {
    /**
     * 自定义结果编码和结果信息
     */
    SUCCESS(200, "操作成功"),
    FAIL(400, "操作失败"),
    SERVICE_ERROR(401, "服务异常"),
    DATA_ERROR(402, "数据异常"),
    PERMISSION(403, "无权访问"),
    REPEAT_SUBMIT(404, "Not Found"),

    ;

    /**
     * 结果编码
     */
    private final Integer resultCode;

    /**
     * 结果信息
     */
    private final String resultMsg;

    ResultCodeEnum(Integer resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }
}
