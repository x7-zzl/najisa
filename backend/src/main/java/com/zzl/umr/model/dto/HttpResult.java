package com.zzl.umr.model.dto;

import com.zzl.umr.enums.ResultCodeEnum;
import lombok.Data;

/**
 * @author zhangzl
 * @date 2025/10/14  23:52
 * @description 返回结果封装
 */
@Data
public class HttpResult<T> {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 信息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public HttpResult() {
    }

    public static <T> HttpResult<T> build(Integer code, String message, T resultData) {

        HttpResult<T> result = new HttpResult<>();

        if (resultData != null) {
            result.setData(resultData);
        }

        result.setCode(code);
        result.setMessage(message);

        return result;
    }

    public static HttpResult success() {
        HttpResult result = new HttpResult();
        result.setCode(ResultCodeEnum.SUCCESS.getResultCode());
        result.setMessage(ResultCodeEnum.SUCCESS.getResultMsg());
        return result;
    }

    public static <T> HttpResult<T> success(T resultData) {
        HttpResult<T> result = build(ResultCodeEnum.SUCCESS.getResultCode(), ResultCodeEnum.SUCCESS.getResultMsg(), resultData);
        return result;
    }

    public static HttpResult fail() {
        HttpResult result = new HttpResult();
        result.setCode(ResultCodeEnum.FAIL.getResultCode());
        result.setMessage(ResultCodeEnum.FAIL.getResultMsg());
        return result;
    }

    public static <T> HttpResult<T> fail(T resultData) {
        return build(ResultCodeEnum.FAIL.getResultCode(), ResultCodeEnum.FAIL.getResultMsg(), resultData);
    }
}
