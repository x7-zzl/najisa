package com.zzl.umr.utils;

import com.zzl.umr.config.exception.CommonServiceException;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangzl
 * @description 通用枚举工具类
 * @date 2026/01/23 15:37:38
 */
public class EnumUtil {

    /**
     * 获取枚举选项列表（通用方法）
     *
     * @param enumClass      枚举类
     * @param codeMethodName 获取code的方法名
     * @param nameMethodName 获取name的方法名
     * @param <T>            枚举类型
     * @return 选项列表
     */
    public static <T extends Enum<T>> List<EnumOption> getEnumOptions(Class<T> enumClass, String codeMethodName, String nameMethodName) {
        List<EnumOption> options = new ArrayList<>();

        try {
            T[] enumConstants = enumClass.getEnumConstants();
            Method getCodeMethod = enumClass.getMethod(codeMethodName);
            Method getNameMethod = enumClass.getMethod(nameMethodName);

            for (T enumConstant : enumConstants) {
                Object code = getCodeMethod.invoke(enumConstant);
                Object name = getNameMethod.invoke(enumConstant);

                options.add(new EnumOption(code, name.toString()));
            }
        } catch (Exception e) {
            throw new RuntimeException("获取枚举选项失败: " + enumClass.getName(), e);
        }

        return options;
    }

    /**
     * 根据code获取枚举显示名称
     *
     * @param enumClass      枚举类
     * @param code           code值
     * @param codeMethodName 获取code的方法名
     * @param nameMethodName 获取name的方法名
     * @param <T>            枚举类型
     * @return 显示名称
     */
    public static <T extends Enum<T>> String getEnumNameByCode(Class<T> enumClass, Integer code, String codeMethodName, String nameMethodName) {
        if (code == null) {
            return "";
        }

        try {
            T[] enumConstants = enumClass.getEnumConstants();
            Method getCodeMethod = enumClass.getMethod(codeMethodName);
            Method getNameMethod = enumClass.getMethod(nameMethodName);

            for (T enumConstant : enumConstants) {
                Object enumCode = getCodeMethod.invoke(enumConstant);
                if (code.equals(enumCode)) {
                    return getNameMethod.invoke(enumConstant).toString();
                }
            }
        } catch (Exception e) {
            throw new CommonServiceException("根据code获取枚举名称失败: " + enumClass.getName(), e);
        }

        return "";
    }

    /**
     * 枚举选项类
     */
    @Data
    public static class EnumOption {
        private Object value;
        private String label;

        public EnumOption(Object value, String label) {
            this.value = value;
            this.label = label;
        }
    }
}