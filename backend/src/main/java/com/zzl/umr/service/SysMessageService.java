package com.zzl.umr.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.umr.model.SysMessage;

import java.util.List;

/**
 * @author zhangzl
 * @description 系统消息表(SysMessage)表服务接口
 * @date 2026-01-30 14:30:09
 */
public interface SysMessageService extends IService<SysMessage> {

    /**
     * 通过ID查询单条数据
     *
     * @param sysMessageId 主键
     * @return 实例对象
     */
    SysMessage queryById(String sysMessageId);

    /**
     * 查询数据列表
     *
     * @param sysMessage 查询参数
     * @return 实例对象集合
     */
    List<SysMessage> queryList(SysMessage sysMessage);

    /**
     * 新增数据
     *
     * @param sysMessage 实例对象
     * @return 是否成功
     */
    boolean add(SysMessage sysMessage);

    /**
     * 批量新增数据
     *
     * @param sysMessageList 数据列表
     * @return 新增数量
     */
    Integer batchAdd(List<SysMessage> sysMessageList);

    /**
     * 修改数据
     *
     * @param sysMessage 实例对象
     * @return 是否成功
     */
    boolean updateById(SysMessage sysMessage);

    /**
     * 通过主键删除数据
     *
     * @param sysMessageId 主键
     * @return 是否成功
     */
    boolean deleteById(String sysMessageId);

    /**
     * 批量删除数据
     *
     * @param idList 主键列表
     * @return 删除数量
     */
    Integer batchDelete(List<String> idList);

    /**
     * 获取当前用户的未读消息数量
     *
     * @param userName 用户名
     * @return 未读数量
     */
    Integer unreadCount(String userName);

    /**
     * 获取当前用户的未读消息列表
     *
     * @param userName 用户名
     * @return 未读消息列表
     */
    List<SysMessage> unread(String userName);

    /**
     * 标记单条消息为已读
     *
     * @param messageId 消息ID
     * @param userName  用户名
     * @return 是否成功
     */
    boolean markRead(String messageId, String userName);

    /**
     * 标记所有消息为已读
     *
     * @param userName 用户名
     * @return 是否成功
     */
    boolean markAllRead(String userName);
}
