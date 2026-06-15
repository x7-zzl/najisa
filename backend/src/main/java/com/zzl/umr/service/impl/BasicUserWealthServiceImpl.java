package com.zzl.umr.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.umr.mapper.BasicUserWealthMapper;
import com.zzl.umr.model.BasicUserWealth;
import com.zzl.umr.service.BasicUserWealthService;
import com.zzl.umr.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
