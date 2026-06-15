package com.zzl.umr.config.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.zzl.umr.constants.MessageConstant.SYS_USER_NAME_STRING;

/**
 * @author zhangzl
 * @description mybatis-plus配置类实现自动填充
 * @date 2025/10/20 2:09
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        //获取当前用户
        String currentUser = getCurrentUser();

        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "createBy", String.class, currentUser);
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateBy", String.class, currentUser);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
    }

    /**
     * 获取当前登录用户
     */
    private String getCurrentUser() {
        return SYS_USER_NAME_STRING;
    }
}