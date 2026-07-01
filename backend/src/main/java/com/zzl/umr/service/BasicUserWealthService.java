package com.zzl.umr.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.umr.model.BasicUserWealth;

import java.util.List;

/**
 * @author zhangzl
 * @description 用户财富信息表(BasicUserWealth)表服务接口
 * @date 2026-01-16 10:41:46
 */
public interface BasicUserWealthService extends IService<BasicUserWealth> {

    /**
     * 通过ID查询单条数据
     *
     * @param basicUserWealthId 主键
     * @return 实例对象
     */
    BasicUserWealth queryById(String basicUserWealthId);

    /**
     * 查询数据列表
     *
     * @param basicUserWealth 查询参数
     * @return 实例对象集合
     */
    List<BasicUserWealth> queryList(BasicUserWealth basicUserWealth);

    /**
     * 新增数据
     *
     * @param basicUserWealth 实例对象
     * @return 是否成功
     */
    boolean add(BasicUserWealth basicUserWealth);

    /**
     * 批量新增数据
     *
     * @param basicUserWealthList 数据列表
     * @return 新增数量
     */
    Integer batchAdd(List<BasicUserWealth> basicUserWealthList);

    /**
     * 修改数据
     *
     * @param basicUserWealth 实例对象
     * @return 是否成功
     */
    boolean updateById(BasicUserWealth basicUserWealth);

    /**
     * 通过主键删除数据
     *
     * @param basicUserWealthId 主键
     * @return 是否成功
     */
    boolean deleteById(String basicUserWealthId);

    /**
     * 批量删除数据
     *
     * @param idList 主键列表
     * @return 删除数量
     */
    Integer batchDelete(List<String> idList);

    /**
     * 每日登录奖励
     * 每天只记录一次
     *
     * @param userId 用户ID
     */
    void processDailyLoginReward(String userId);

    /**
     * 初始化用户财富
     *
     * @param userId 用户ID
     */
    void initUserWealth(String userId);

    /**
     * 查询用户今日是否已签到
     *
     * @param userId 用户ID
     * @return true-已签到，false-未签到
     */
    boolean hasSignedInToday(String userId);
}
