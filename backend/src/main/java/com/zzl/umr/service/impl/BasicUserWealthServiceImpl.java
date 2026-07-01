package com.zzl.umr.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.umr.mapper.BasicUserWealthMapper;
import com.zzl.umr.model.BasicUserWealth;
import com.zzl.umr.service.BasicUserWealthService;
import com.zzl.umr.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author zhangzl
 * @description 用户财富信息表(BasicUserWealth)表服务实现类
 * @date 2026-01-16 10:41:46
 */
@Service("basicUserWealthService")
@Slf4j
public class BasicUserWealthServiceImpl extends ServiceImpl<BasicUserWealthMapper, BasicUserWealth> implements BasicUserWealthService {
    @Resource
    private BasicUserWealthMapper basicUserWealthMapper;

    /**
     * 通过ID查询单条数据
     *
     * @param basicUserWealthId 主键
     * @return 实例对象
     */
    @Override
    public BasicUserWealth queryById(String basicUserWealthId) {
        return basicUserWealthMapper.selectById(basicUserWealthId);
    }

    /**
     * 查询数据列表
     *
     * @param basicUserWealth 查询参数
     * @return 实例对象集合
     */
    @Override
    public List<BasicUserWealth> queryList(BasicUserWealth basicUserWealth) {
        // 处理查询条件
        LambdaQueryWrapper<BasicUserWealth> queryWrapper = new LambdaQueryWrapper<>();
        this.dealWrapper(queryWrapper, basicUserWealth);

        List<BasicUserWealth> basicUserWealthList = basicUserWealthMapper.selectList(queryWrapper);

        // 处理数据
        this.dealDataList(basicUserWealthList);

        return basicUserWealthList;
    }

    /**
     * 新增数据
     *
     * @param basicUserWealth 实例对象
     * @return 是否成功
     */
    @Override
    public boolean add(BasicUserWealth basicUserWealth) {
        basicUserWealth.setId(SnowflakeIdWorker.newId());
        log.info("插入一条数据:{}", basicUserWealth);
        return basicUserWealthMapper.insert(basicUserWealth) > 0;
    }

    /**
     * 批量新增数据
     *
     * @param basicUserWealthList 数据列表
     * @return 新增数量
     */
    @Override
    public Integer batchAdd(List<BasicUserWealth> basicUserWealthList) {
        if (CollectionUtil.isNotEmpty(basicUserWealthList)) {
            log.info("批量插入{}条数据", basicUserWealthList.size());

            for (BasicUserWealth basicUserWealth : basicUserWealthList) {
                basicUserWealth.setId(SnowflakeIdWorker.newId());
            }

            boolean saveBatch = this.saveBatch(basicUserWealthList);
            return saveBatch ? basicUserWealthList.size() : 0;
        }
        return 0;
    }

    /**
     * 修改数据
     *
     * @param basicUserWealth 实例对象
     * @return 是否成功
     */
    @Override
    public boolean updateById(BasicUserWealth basicUserWealth) {
        log.info("修改一条数据:{}", basicUserWealth);
        return basicUserWealthMapper.updateById(basicUserWealth) > 0;
    }

    /**
     * 通过主键删除数据
     *
     * @param basicUserWealthId 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(String basicUserWealthId) {
        log.info("删除一条数据，Id:{}", basicUserWealthId);
        return basicUserWealthMapper.deleteById(basicUserWealthId) > 0;
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
            return basicUserWealthMapper.deleteBatchIds(idList);
        }
        return 0;
    }

    /**
     * 处理每日登录奖励
     * 每天只记录一次登录奖励
     *
     * @param userId 用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processDailyLoginReward(String userId) {
        // 查询用户财富信息
        LambdaQueryWrapper<BasicUserWealth> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BasicUserWealth::getUserId, userId).last("limit 1");
        BasicUserWealth wealth = basicUserWealthMapper.selectOne(queryWrapper);

        if (wealth == null) {
            // 如果财富记录不存在，先初始化
            log.warn("用户{}财富记录不存在，执行初始化", userId);
            initUserWealth(userId);
            return;
        }

        // 获取今天的日期
        Date today = DateUtil.beginOfDay(new Date());

        // 检查今天是否已经领取过奖励
        if (wealth.getLastRewardDate() != null) {
            Date lastRewardDay = DateUtil.beginOfDay(wealth.getLastRewardDate());
            if (DateUtil.isSameDay(lastRewardDay, today)) {
                log.info("用户{}今天已领取过每日登录奖励，跳过", userId);
                return;
            }
        }

        // 发放奖励：元石+1，经验+10
        double newOriginStone = (wealth.getOriginStone() != null ? wealth.getOriginStone() : 0) + 1;
        long newExperience = (wealth.getExperience() != null ? wealth.getExperience() : 0) + 10;

        wealth.setOriginStone(newOriginStone);
        wealth.setExperience(newExperience);
        wealth.setLastRewardDate(today);

        basicUserWealthMapper.updateById(wealth);

        log.info("用户{}每日登录奖励发放成功：元石+1（当前：{}），经验+10（当前：{}）", userId, newOriginStone, newExperience);
    }

    /**
     * 初始化用户财富
     *
     * @param userId 用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initUserWealth(String userId) {
        BasicUserWealth wealth = new BasicUserWealth();
        wealth.setId(SnowflakeIdWorker.newId());
        wealth.setUserId(userId);
        wealth.setLevel(1);
        wealth.setOriginStone(0.0);
        wealth.setImmortalOriginStone(0.0);
        wealth.setExperience(0L);

        basicUserWealthMapper.insert(wealth);

        log.info("用户{}财富初始化成功：等级=1，元石=0，经验=0", userId);
    }

    /**
     * 处理查询条件
     *
     * @param queryWrapper    查询条件
     * @param basicUserWealth 查询参数
     */
    private void dealWrapper(LambdaQueryWrapper<BasicUserWealth> queryWrapper, BasicUserWealth basicUserWealth) {
    }

    /**
     * 处理数据列表
     *
     * @param basicUserWealthList 数据列表
     */
    private void dealDataList(List<BasicUserWealth> basicUserWealthList) {
        if (CollectionUtil.isNotEmpty(basicUserWealthList)) {
        }
    }
}
