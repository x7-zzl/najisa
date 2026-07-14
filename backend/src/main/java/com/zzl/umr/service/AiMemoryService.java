package com.zzl.umr.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.umr.model.AiUserMemory;

import java.util.List;

/**
 * @author zhangzl
 * @date 2026/7/13  17:35
 * @description AI长期记忆表服务接口
 */
public interface AiMemoryService extends IService<AiUserMemory> {

    /**
     * 查询用户对某角色的近期记忆，按重要性+时间排序，最多5条
     *
     * @param userId      用户ID
     * @param characterId 角色ID
     * @return 记忆列表
     */
    List<AiUserMemory> queryRecentMemories(String userId, String characterId);

    /**
     * 获取记忆摘要列表，供Python使用
     *
     * @param userId      用户ID
     * @param characterId 角色ID
     * @return 摘要字符串列表
     */
    List<String> getMemorySummaries(String userId, String characterId);

    /**
     * 保存一条新记忆
     *
     * @param userId      用户ID
     * @param characterId 角色ID
     * @param type        类型
     * @param summary     摘要
     * @param importance  重要性1-10
     */
    void saveMemory(String userId, String characterId, String type, String summary, Integer importance);
}
