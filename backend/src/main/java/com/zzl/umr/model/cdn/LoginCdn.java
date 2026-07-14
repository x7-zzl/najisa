package com.zzl.umr.model.cdn;

import lombok.Data;

/**
 * @author zhangzl
 * @date 2025/12/6  23:25
 * @description 登录 / 注册请求参数
 */
@Data
public class LoginCdn {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 类型：1-登录，2-注册
     */
    private String loginType;

}
