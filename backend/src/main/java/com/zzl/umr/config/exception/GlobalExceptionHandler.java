package com.zzl.umr.config.exception;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zzl.umr.enums.ResultCodeEnum;
import com.zzl.umr.model.dto.HttpResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author zhangzl
 * @description 全局异常处理类
 * @date 2025/10/20 2:03
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理系统异常
     */
    @ExceptionHandler(value = Exception.class)
    public HttpResult<?> handleSystemException(Exception e) {

        // 如果异常信息不为空，且不是空字符串，则显示异常信息
        if (StringUtils.isNotEmpty(e.getMessage())) {
            return HttpResult.fail(e.getMessage());
        }

        // 否则显示默认的操作失败信息
        return HttpResult.fail(ResultCodeEnum.FAIL.getResultMsg());
    }

    /**
     * 处理认证异常（包括UsernameNotFoundException）
     */
    @ExceptionHandler(value = AuthenticationException.class)
    public HttpResult<?> handleAuthenticationException(AuthenticationException e) {
        // 直接返回认证异常的具体信息
        if (StringUtils.isNotEmpty(e.getMessage())) {
            return HttpResult.fail(e.getMessage());
        }

        return HttpResult.fail("认证失败");
    }

    /**
     * 处理权限不足异常
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    public HttpResult<?> handleAccessDeniedException(AccessDeniedException e) {
        return HttpResult.fail(ResultCodeEnum.PERMISSION.getResultMsg());
    }

}