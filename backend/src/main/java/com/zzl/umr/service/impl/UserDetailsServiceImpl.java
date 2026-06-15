package com.zzl.umr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zzl.umr.config.exception.CommonServiceException;
import com.zzl.umr.enums.UserDisEnabledEnum;
import com.zzl.umr.mapper.BasicUserMapper;
import com.zzl.umr.model.BasicUser;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * @author zhangzl
 * @description 实现 Spring Security 的标准接口
 * @date 2026/01/21 14:43:07
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private BasicUserMapper basicUserMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 现有的逻辑查数据库
        LambdaQueryWrapper<BasicUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BasicUser::getUserName, username);
        queryWrapper.last("limit 1");
        BasicUser user = basicUserMapper.selectOne(queryWrapper);

        // 如果没查到，抛出安全框架异常
        if (user == null) {
            throw new CommonServiceException("用户名不存在");
        }

        // 账号存在，但是被封禁
        if (UserDisEnabledEnum.DISABLED.getCode().equals(user.getIsEnabled())) {
            throw new CommonServiceException("用户被禁用");
        }

        // 返回 Security 的 User 对象（包含：用户名、加密后的密码、权限列表）
        // 这里暂时给个空的 ArrayList 表示没有任何权限
        return new User(user.getUserName(), user.getPassword(), new ArrayList<>());
    }
}