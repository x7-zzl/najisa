package com.zzl.umr.utils;

import org.springframework.util.StringUtils;

/**
 * @author zhangzl
 * @description 密码校验工具类
 * @date 2025/12/07 01:26:56
 */
public class PasswordValidatorUtil {
    // 数字正则
    private static final String REG_NUMBER = ".*\\d+.*";
    // 字母正则（大小写）
    private static final String REG_LETTER = ".*[a-zA-Z]+.*";
    // 特殊字符正则（非数字、非字母）
    private static final String REG_SPECIAL = ".*[^a-zA-Z0-9]+.*";


    /**
     * 校验密码复杂度
     * 规则：长度≥8位 + 包含至少两种类型（数字、字母、特殊字符）
     *
     * @param password 密码
     * @return 校验结果（null表示通过，否则返回错误提示）
     */
    public static String validatePassword(String password) {
        // 1. 非空校验
        if (!StringUtils.hasText(password)) {
            return "密码不能为空";
        }
        // 2. 长度校验
        if (password.length() < 8) {
            return "密码长度不能小于8位";
        }
        // 3. 统计密码包含的类型数（数字、字母、特殊字符）
        int typeCount = 0;
        if (password.matches(REG_NUMBER)) {
            typeCount++;
        }
        if (password.matches(REG_LETTER)) {
            typeCount++;
        }
        if (password.matches(REG_SPECIAL)) {
            typeCount++;
        }
        // 4. 校验是否至少包含两种类型
        if (typeCount < 2) {
            return "密码需包含数字、字母、特殊字符中的至少两种组合";
        }
        // 校验通过
        return null;
    }
}
