package com.zzl.umr.constants;

import java.time.format.DateTimeFormatter;

/**
 * @author zhangzl
 * @description 信息常亮类
 * @date 2025/10/15 1:30
 */
public class MessageConstant {
    public static final String ADD_SUCCESS_MSG = "添加成功";
    public static final String UPDATE_SUCCESS_MSG = "修改成功";
    public static final String DELETE_SUCCESS_MSG = "删除成功";
    public static final String USER_NOT_EXIST = "用户不存在";
    public static final String USER_EXIST = "用户已存在";
    public static final String USER_LOGIN_ERROR = "用户名或密码错误";
    public static final String USER_LOGOUT_ERROR = "用户未登录";
    public static final String USER_LOGOUT_SUCCESS = "用户退出成功";
    public static final String USER_LOGOUT_ERROR_MSG = "用户已注销";

    //系统用户
    public static final String SYS_USER_NAME_STRING = "sys_user";

    // 枚举类通用getCode
    public static final String ENUM_GET_CODE_METHOD_NAME = "getCode";
    // 枚举类通用getName
    public static final String ENUM_GET_NAME_METHOD_NAME = "getName";

    // 添加
    public static final String ADD_STRING = "add";
    // 修改
    public static final String UPDATE_STRING = "update";
    // 删除
    public static final String DELETE_STRING = "delete";

    // 每日浏览量阈值
    public static final int HOT_THRESHOLD = 100;
    // 日期格式
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

}
