package com.zzl.umr.model.cdn;

import lombok.Data;

/**
 * @author zhangzl
 * @description 登录，注册
 * @date 2025/12/06 23:25:18
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
