package com.zzl.umr.config.exception;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhangzl
 * @description 通用业务异常封装，隐藏掉异常信息，只返回网络问题
 * @date 2026/01/15 16:23:19
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommonServiceException extends RuntimeException {

    private Integer code;

    private String msg;

    public CommonServiceException() {
        super("网络异常");
        this.code = 500;
        this.msg = "网络异常";
    }

    public CommonServiceException(String msg, Object... arguments) {
        super(StrUtil.format(msg, arguments));
        this.code = 500;
        this.msg = StrUtil.format(msg, arguments);
    }

    public CommonServiceException(Integer code, String msg, Object... arguments) {
        super(StrUtil.format(msg, arguments));
        this.code = code;
        this.msg = StrUtil.format(msg, arguments);
    }
}
