package com.zzl.umr.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.umr.model.AiUserEmotion;

import java.util.Map;

/**
 * @author zhangzl
 * @date 2026/7/13  17:35
 * @description AI用户情绪表服务接口
 */
public interface AiEmotionService extends IService<AiUserEmotion> {

    /**
     * 获取或初始化用户对某角色的情绪状态
     *
     * @param userId      用户ID
     * @param characterId 角色ID
     * @return 情绪状态
     */
    AiUserEmotion getOrInit(String userId, String characterId);

    /**
     * 根据聊天内容更新情绪值
     *
     * @param userId      用户ID
     * @param characterId 角色ID
     * @param userMessage 用户消息内容
     */
    void updateAfterChat(String userId, String characterId, String userMessage);

    /**
     * 获取情绪快照，转为Map传给Python
     *
     * @param userId      用户ID
     * @param characterId 角色ID
     * @return 情绪Map
     */
    Map<String, Integer> getEmotionSnapshot(String userId, String characterId);
}
