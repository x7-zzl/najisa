package com.zzl.umr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.umr.mapper.AiUserMemoryMapper;
import com.zzl.umr.model.AiUserMemory;
import com.zzl.umr.service.AiMemoryService;
import com.zzl.umr.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangzl
 * @date 2026/7/13  17:35
 * @description AI长期记忆表服务实现类
 */
@Slf4j
@Service("aiMemoryService")
public class AiMemoryServiceImpl extends ServiceImpl<AiUserMemoryMapper, AiUserMemory> implements AiMemoryService {

    @Resource
    private AiUserMemoryMapper aiUserMemoryMapper;

    /**
     * 查询用户对某角色的近期记忆，按重要性+时间排序，最多5条
     *
     * @param userId      用户ID
     * @param characterId 角色ID
     * @return 记忆列表
     */
    @Override
    public List<AiUserMemory> queryRecentMemories(String userId, String characterId) {
        LambdaQueryWrapper<AiUserMemory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiUserMemory::getUserId, userId);
        wrapper.eq(AiUserMemory::getCharacterId, characterId);
        wrapper.orderByDesc(AiUserMemory::getImportance);
        wrapper.orderByDesc(AiUserMemory::getCreateTime);
        wrapper.last("limit 5");
        return aiUserMemoryMapper.selectList(wrapper);
    }

    /**
     * 获取记忆摘要列表，供Python使用
     *
     * @param userId      用户ID
     * @param characterId 角色ID
     * @return 摘要字符串列表
     */
    @Override
    public List<String> getMemorySummaries(String userId, String characterId) {
        return queryRecentMemories(userId, characterId)
                .stream()
                .map(AiUserMemory::getSummary)
                .collect(Collectors.toList());
    }

    /**
     * 保存一条新记忆
     *
     * @param userId      用户ID
     * @param characterId 角色ID
     * @param type        类型
     * @param summary     摘要
     * @param importance  重要性1-10
     */
    @Override
    public void saveMemory(String userId, String characterId, String type, String summary, Integer importance) {
        LambdaQueryWrapper<AiUserMemory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiUserMemory::getUserId, userId);
        wrapper.eq(AiUserMemory::getCharacterId, characterId);
        wrapper.eq(AiUserMemory::getSummary, summary);
        wrapper.last("limit 1");
        if (aiUserMemoryMapper.selectOne(wrapper) != null) {
            return;
        }

        AiUserMemory memory = new AiUserMemory();
        memory.setId(SnowflakeIdWorker.newId());
        memory.setUserId(userId);
        memory.setCharacterId(characterId);
        memory.setType(type);
        memory.setSummary(summary);
        memory.setImportance(importance != null ? importance : 1);
        memory.setTriggerCount(0);
        memory.setCreateTime(new Date());
        memory.setUpdateTime(new Date());
        aiUserMemoryMapper.insert(memory);

        log.info("保存新记忆: userId={}, type={}, summary={}", userId, type, summary);
    }
}
