package com.zzl.umr.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.umr.mapper.BasicUserLoginMapper;
import com.zzl.umr.mapper.BasicUserMapper;
import com.zzl.umr.model.BasicUser;
import com.zzl.umr.model.BasicUserLogin;
import com.zzl.umr.service.BasicUserLoginService;
import com.zzl.umr.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author zhangzl
 * @description (BasicUserLogin)表服务实现类
 * @date 2025-10-20 22:09:48
 */
@Service("basicUserLoginService")
@Slf4j
public class BasicUserLoginServiceImpl extends ServiceImpl<BasicUserLoginMapper, BasicUserLogin> implements BasicUserLoginService {
    @Resource
    private BasicUserLoginMapper basicUserLoginMapper;

    @Resource
    private BasicUserMapper basicUserMapper;

    /**
     * 通过ID查询单条数据
     *
     * @param basicUserLoginId 主键
     * @return 实例对象
     */
    @Override
    public BasicUserLogin queryById(String basicUserLoginId) {
        return basicUserLoginMapper.selectById(basicUserLoginId);
    }

    /**
     * 查询数据列表
     *
     * @param basicUserLogin 查询参数
     * @return 实例对象集合
     */
    @Override
    public List<BasicUserLogin> queryList(BasicUserLogin basicUserLogin) {
        // 处理查询条件
        LambdaQueryWrapper<BasicUserLogin> queryWrapper = new LambdaQueryWrapper<>();
        this.dealWrapper(queryWrapper, basicUserLogin);

        List<BasicUserLogin> basicUserLoginList = basicUserLoginMapper.selectList(queryWrapper);

        // 处理数据
        this.dealDataList(basicUserLoginList);

        return basicUserLoginList;
    }

    /**
     * 新增数据
     *
     * @param basicUserLogin 实例对象
     * @return 是否成功
     */
    @Override
    public boolean add(BasicUserLogin basicUserLogin) {
        basicUserLogin.setId(SnowflakeIdWorker.newId());
        log.info("插入一条数据:{}", basicUserLogin);
        return basicUserLoginMapper.insert(basicUserLogin) > 0;
    }

    /**
     * 批量新增数据
     *
     * @param basicUserLoginList 数据列表
     * @return 新增数量
     */
    @Override
    public Integer batchAdd(List<BasicUserLogin> basicUserLoginList) {
        if (CollectionUtil.isNotEmpty(basicUserLoginList)) {
            log.info("批量插入{}条数据", basicUserLoginList.size());

            for (BasicUserLogin basicUserLogin : basicUserLoginList) {
                basicUserLogin.setId(SnowflakeIdWorker.newId());
            }

            boolean saveBatch = this.saveBatch(basicUserLoginList);
            return saveBatch ? basicUserLoginList.size() : 0;
        }
        return 0;
    }

    /**
     * 修改数据
     *
     * @param basicUserLogin 实例对象
     * @return 是否成功
     */
    @Override
    public boolean updateById(BasicUserLogin basicUserLogin) {
        log.info("修改一条数据:{}", basicUserLogin);
        return basicUserLoginMapper.updateById(basicUserLogin) > 0;
    }

    /**
     * 通过主键删除数据
     *
     * @param basicUserLoginId 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(String basicUserLoginId) {
        log.info("删除一条数据，Id:{}", basicUserLoginId);
        return basicUserLoginMapper.deleteById(basicUserLoginId) > 0;
    }

    /**
     * 批量删除数据
     *
     * @param idList 主键列表
     * @return 删除数量
     */
    @Override
    public Integer batchDelete(List<String> idList) {
        if (CollectionUtil.isNotEmpty(idList)) {
            log.info("批量删除{}条数据", idList.size());
            return basicUserLoginMapper.deleteBatchIds(idList);
        }
        return 0;
    }


    /**
     * 异步更新登录信息
     *
     * @param userName 用户名
     */
    @Async("loginLogExecutor")
    public void asyncUpdateLoginInfo(String userName) {
        try {
            updateLoginInfo(userName);
        } catch (Exception e) {
            log.error("异步更新登录日志失败，用户名：{}，错误：{}", userName, e.getMessage(), e);
        }
    }


    /**
     * 更新登录信息
     *
     * @param userName 用户名
     */
    public void updateLoginInfo(String userName) {
        LambdaQueryWrapper<BasicUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BasicUser::getUserName, userName).last("limit 1");
        BasicUser user = basicUserMapper.selectOne(queryWrapper);

        if (user != null) {
            Date now = new Date();
            LambdaQueryWrapper<BasicUserLogin> loginQueryWrapper = new LambdaQueryWrapper<>();
            loginQueryWrapper.eq(BasicUserLogin::getUserId, user.getId()).last("limit 1");
            BasicUserLogin userLogin = basicUserLoginMapper.selectOne(loginQueryWrapper);

            if (userLogin == null) {
                userLogin = new BasicUserLogin();
                userLogin.setId(SnowflakeIdWorker.newId());
                userLogin.setUserId(user.getId());
                userLogin.setFirstLoginTime(now);
                userLogin.setLastLoginTime(now);
                basicUserLoginMapper.insert(userLogin);
            } else {
                userLogin.setLastLoginTime(now);
                basicUserLoginMapper.updateById(userLogin);
            }
        }
    }


    /**
     * 处理查询条件
     *
     * @param queryWrapper   查询条件
     * @param basicUserLogin 查询参数
     */
    private void dealWrapper(LambdaQueryWrapper<BasicUserLogin> queryWrapper, BasicUserLogin basicUserLogin) {
    }

    /**
     * 处理数据列表
     *
     * @param basicUserLoginList 数据列表
     */
    private void dealDataList(List<BasicUserLogin> basicUserLoginList) {
        if (CollectionUtil.isNotEmpty(basicUserLoginList)) {
        }
    }
}
