package com.zzl.umr.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.umr.mapper.SysMessageMapper;
import com.zzl.umr.mapper.SysMessageReadMapper;
import com.zzl.umr.model.SysMessage;
import com.zzl.umr.model.SysMessageRead;
import com.zzl.umr.service.SysMessageService;
import com.zzl.umr.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhangzl
 * @description 系统消息表(SysMessage)表服务实现类
 * @date 2026-01-30 14:30:09
 */
@Service("sysMessageService")
@Slf4j
public class SysMessageServiceImpl extends ServiceImpl<SysMessageMapper, SysMessage> implements SysMessageService {
    @Resource
    private SysMessageMapper sysMessageMapper;

    @Resource
    private SysMessageReadMapper sysMessageReadMapper;

    /**
     * 通过ID查询单条数据
     *
     * @param sysMessageId 主键
     * @return 实例对象
     */
    @Override
    public SysMessage queryById(String sysMessageId) {
        return sysMessageMapper.selectById(sysMessageId);
    }

    /**
     * 查询数据列表
     *
     * @param sysMessage 查询参数
     * @return 实例对象集合
     */
    @Override
    public List<SysMessage> queryList(SysMessage sysMessage) {
        LambdaQueryWrapper<SysMessage> queryWrapper = new LambdaQueryWrapper<>();
        this.dealWrapper(queryWrapper, sysMessage);
        List<SysMessage> sysMessageList = sysMessageMapper.selectList(queryWrapper);
        this.dealDataList(sysMessageList);
        return sysMessageList;
    }

    /**
     * 新增数据
     *
     * @param sysMessage 实例对象
     * @return 是否成功
     */
    @Override
    public boolean add(SysMessage sysMessage) {
        sysMessage.setId(SnowflakeIdWorker.newId());
        log.info("插入一条数据:{}", sysMessage);
        return sysMessageMapper.insert(sysMessage) > 0;
    }

    /**
     * 批量新增数据
     *
     * @param sysMessageList 数据列表
     * @return 新增数量
     */
    @Override
    public Integer batchAdd(List<SysMessage> sysMessageList) {
        if (CollectionUtil.isNotEmpty(sysMessageList)) {
            log.info("批量插入{}条数据", sysMessageList.size());

            for (SysMessage sysMessage : sysMessageList) {
                sysMessage.setId(SnowflakeIdWorker.newId());
            }

            boolean saveBatch = this.saveBatch(sysMessageList);
            return saveBatch ? sysMessageList.size() : 0;
        }
        return 0;
    }

    /**
     * 修改数据
     *
     * @param sysMessage 实例对象
     * @return 是否成功
     */
    @Override
    public boolean updateById(SysMessage sysMessage) {
        log.info("修改一条数据:{}", sysMessage);
        return sysMessageMapper.updateById(sysMessage) > 0;
    }

    /**
     * 通过主键删除数据
     *
     * @param sysMessageId 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(String sysMessageId) {
        log.info("删除一条数据，Id:{}", sysMessageId);
        return sysMessageMapper.deleteById(sysMessageId) > 0;
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
            return sysMessageMapper.deleteBatchIds(idList);
        }
        return 0;
    }

    /**
     * 获取当前用户的未读消息数量
     * 查询所有消息中，该用户没有已读记录的数量
     *
     * @param userName 用户名
     * @return 未读数量
     */
    @Override
    public Integer unreadCount(String userName) {
        // 查询该用户已读的消息ID集合
        Set<String> readMessageIds = getReadMessageIds(userName);

        // 查询所有消息数量
        long totalCount = sysMessageMapper.selectCount(null);

        // 未读数 = 总数 - 已读数
        return (int) (totalCount - readMessageIds.size());
    }

    /**
     * 获取当前用户的全部消息列表（带已读状态）
     * 排序：未读优先，同状态按时间倒序
     *
     * @param userName 用户名
     * @return 全部消息列表（isRead: 0未读 1已读）
     */
    @Override
    public List<SysMessage> unread(String userName) {
        // 查询该用户已读的消息ID集合
        Set<String> readMessageIds = getReadMessageIds(userName);

        // 查询所有消息，按创建时间倒序
        LambdaQueryWrapper<SysMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(SysMessage::getCreateTime);
        List<SysMessage> allMessages = sysMessageMapper.selectList(queryWrapper);

        // 设置每条消息的已读状态
        allMessages.forEach(msg -> {
            if (readMessageIds.contains(msg.getId())) {
                msg.setIsRead(1);
            } else {
                msg.setIsRead(0);
            }
        });

        // 排序：未读(0)在前，已读(1)在后；同状态按创建时间倒序（已在SQL中排好）
        allMessages.sort((a, b) -> {
            if (!a.getIsRead().equals(b.getIsRead())) {
                return a.getIsRead().compareTo(b.getIsRead());
            }
            // 同状态的已经按时间倒序，保持原有顺序
            return 0;
        });

        return allMessages;
    }

    /**
     * 标记单条消息为已读
     *
     * @param messageId 消息ID
     * @param userName  用户名
     * @return 是否成功
     */
    @Override
    public boolean markRead(String messageId, String userName) {
        // 检查是否已存在已读记录（防止重复插入）
        LambdaQueryWrapper<SysMessageRead> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMessageRead::getMessageId, messageId)
                .eq(SysMessageRead::getUserName, userName);
        Long count = sysMessageReadMapper.selectCount(queryWrapper);
        if (count > 0) {
            return true;
        }

        SysMessageRead readRecord = new SysMessageRead();
        readRecord.setId(SnowflakeIdWorker.newId());
        readRecord.setMessageId(messageId);
        readRecord.setUserName(userName);
        readRecord.setReadTime(new Date());

        return sysMessageReadMapper.insert(readRecord) > 0;
    }

    /**
     * 标记所有消息为已读
     *
     * @param userName 用户名
     * @return 是否成功
     */
    @Override
    public boolean markAllRead(String userName) {
        // 获取该用户已读的消息ID
        Set<String> readMessageIds = getReadMessageIds(userName);

        // 查询所有消息
        List<SysMessage> allMessages = sysMessageMapper.selectList(null);

        // 筛选出未读消息，批量插入已读记录
        List<SysMessageRead> newReadRecords = allMessages.stream()
                .filter(msg -> !readMessageIds.contains(msg.getId()))
                .map(msg -> {
                    SysMessageRead readRecord = new SysMessageRead();
                    readRecord.setId(SnowflakeIdWorker.newId());
                    readRecord.setMessageId(msg.getId());
                    readRecord.setUserName(userName);
                    readRecord.setReadTime(new Date());
                    return readRecord;
                })
                .collect(Collectors.toList());

        if (CollectionUtil.isNotEmpty(newReadRecords)) {
            for (SysMessageRead record : newReadRecords) {
                sysMessageReadMapper.insert(record);
            }
        }

        return true;
    }

    /**
     * 获取用户已读的消息ID集合
     *
     * @param userName 用户名
     * @return 已读消息ID集合
     */
    private Set<String> getReadMessageIds(String userName) {
        LambdaQueryWrapper<SysMessageRead> readWrapper = new LambdaQueryWrapper<>();
        readWrapper.eq(SysMessageRead::getUserName, userName)
                .select(SysMessageRead::getMessageId);
        List<SysMessageRead> readRecords = sysMessageReadMapper.selectList(readWrapper);
        return readRecords.stream()
                .map(SysMessageRead::getMessageId)
                .collect(Collectors.toSet());
    }

    /**
     * 处理查询条件
     */
    private void dealWrapper(LambdaQueryWrapper<SysMessage> queryWrapper, SysMessage sysMessage) {
    }

    /**
     * 处理数据列表
     */
    private void dealDataList(List<SysMessage> sysMessageList) {
        if (CollectionUtil.isNotEmpty(sysMessageList)) {
        }
    }
}
