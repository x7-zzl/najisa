package com.zzl.umr.utils;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.zzl.umr.config.exception.CommonServiceException;
/**
 * @author zhangzl
 * @description 通用邮件工具类
 * @date 2026/02/02 11:31:01
 */
public class CommonEmailUtil {

    /**
     * 判断是否邮箱
     **/
    public static boolean isEmail(String email) {
        return Validator.isEmail(email);
    }

    /**
     * 校验邮箱格式
     **/
    public static void validEmail(String emails) {
        StrUtil.split(emails, StrUtil.COMMA).forEach(email -> {
            if (!isEmail(email)) {
                throw new CommonServiceException("邮件地址：{}格式错误", email);
            }
        });
    }
}
