package com.zzl.umr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.umr.mapper.AiUserRelationshipMapper;
import com.zzl.umr.model.AiUserRelationship;
import com.zzl.umr.service.AiRelationshipService;
import com.zzl.umr.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zhangzl
 * @date 2026/7/13  17:35
 * @description AI用户关系表服务实现类
 */
@Slf4j
@Service("aiRelationshipService")
public class AiRelationshipServiceImpl extends ServiceImpl<AiUserRelationshipMapper, AiUserRelationship>
        implements AiRelationshipService {

    private static final String[] LEVEL_NAMES = { "陌生人", "朋友", "暧昧", "恋爱", "热恋", "稳定", "同居" };

    @Resource
    private AiUserRelationshipMapper aiUserRelationshipMapper;

    /**
     * 获取或初始化用户对某角色的关系
     *
     * @param userId      用户ID
     * @param characterId 角色ID
     * @return 关系状态
     */
    @Override
    public AiUserRelationship getOrInit(String userId, String characterId) {
        LambdaQueryWrapper<AiUserRelationship> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiUserRelationship::getUserId, userId);
        wrapper.eq(AiUserRelationship::getCharacterId, characterId);
        wrapper.last("limit 1");
        AiUserRelationship rel = aiUserRelationshipMapper.selectOne(wrapper);

        if (rel == null) {
            rel = new AiUserRelationship();
            rel.setId(SnowflakeIdWorker.newId());
            rel.setUserId(userId);
            rel.setCharacterId(characterId);
            rel.setLevel(0);
            rel.setIntimacy(0);
            rel.setNickname("");
            rel.setTotalChats(0);
            rel.setCreateTime(new Date());
            rel.setUpdateTime(new Date());
            aiUserRelationshipMapper.insert(rel);
        }

        return rel;
    }

    /**
     * 每次聊天后更新关系，亲密度+1，聊天次数+1
     *
     * @param userId      用户ID
     * @param characterId 角色ID
     */
    @Override
    public void updateAfterChat(String userId, String characterId) {
        AiUserRelationship rel = getOrInit(userId, characterId);

        rel.setTotalChats(rel.getTotalChats() + 1);

        int newIntimacy = Math.min(100, rel.getIntimacy() + 1);
        rel.setIntimacy(newIntimacy);

        int newLevel = calcLevel(newIntimacy);
        if (newLevel > rel.getLevel()) {
            rel.setLevel(newLevel);
            log.info("用户 {} 与角色 {} 关系升级: level={}, intimacy={}", userId, characterId, newLevel, newIntimacy);
        }

        rel.setUpdateTime(new Date());
        aiUserRelationshipMapper.updateById(rel);
    }

    /**
     * 获取关系快照，转为Map传给Python
     *
     * @param userId      用户ID
     * @param characterId 角色ID
     * @return 关系Map
     */
    @Override
    public Map<String, Object> getRelationshipSnapshot(String userId, String characterId) {
        AiUserRelationship rel = getOrInit(userId, characterId);
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("level", rel.getLevel());
        snapshot.put("levelName", LEVEL_NAMES[Math.min(rel.getLevel(), LEVEL_NAMES.length - 1)]);
        snapshot.put("intimacy", rel.getIntimacy());
        snapshot.put("nickname", rel.getNickname() != null ? rel.getNickname() : "");
        snapshot.put("totalChats", rel.getTotalChats());
        return snapshot;
    }

    /**
     * 根据亲密度计算关系等级
     *
     * @param intimacy 亲密度
     * @return 关系等级（0-6）
     */
    private int calcLevel(int intimacy) {
        if (intimacy >= 90) return 6;
        if (intimacy >= 80) return 5;
        if (intimacy >= 65) return 4;
        if (intimacy >= 45) return 3;
        if (intimacy >= 25) return 2;
        if (intimacy >= 10) return 1;
        return 0;
    }
}
