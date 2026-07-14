package com.zzl.umr.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.umr.model.AiUserRelationship;

import java.util.Map;

/**
 * @author zhangzl
 * @date 2026/7/13  17:35
 * @description AI用户关系表服务接口
 */
public interface AiRelationshipService extends IService<AiUserRelationship> {

    /**
     * 获取或初始化用户对某角色的关系
     *
     * @param userId      用户ID
     * @param characterId 角色ID
     * @return 关系状态
     */
    AiUserRelationship getOrInit(String userId, String characterId);

    /**
     * 每次聊天后更新关系，亲密度+1，聊天次数+1
     *
     * @param userId      用户ID
     * @param characterId 角色ID
     */
    void updateAfterChat(String userId, String characterId);

    /**
     * 获取关系快照，转为Map传给Python
     *
     * @param userId      用户ID
     * @param characterId 角色ID
     * @return 关系Map
     */
    Map<String, Object> getRelationshipSnapshot(String userId, String characterId);
}
