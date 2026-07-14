package com.zzl.umr.service;

import com.zzl.umr.model.AiChatMessage;
import com.zzl.umr.model.cdn.SendMessageCdn;
import com.zzl.umr.model.dto.ChatSessionVO;
import com.zzl.umr.model.dto.SendMessageVO;

import java.util.List;

/**
 * @author zhangzl
 * @date 2026/7/13  17:35
 * @description AI聊天核心服务接口，编排整个聊天流程
 */
public interface AIChatService {

    /**
     * 发送消息，核心流程：保存消息、调用Python、更新情绪关系
     *
     * @param cdn 请求参数
     * @return 发送结果
     */
    SendMessageVO sendMessage(SendMessageCdn cdn);

    /**
     * 获取当前用户的会话列表
     *
     * @return 会话列表
     */
    List<ChatSessionVO> getMySessions();

    /**
     * 获取指定会话的消息列表
     *
     * @param sessionId 会话ID
     * @return 消息列表
     */
    List<AiChatMessage> getSessionMessages(String sessionId);

    /**
     * 新建会话
     *
     * @param characterId 角色ID
     * @return 新会话ID
     */
    String createSession(String characterId);

    /**
     * 删除会话，软删除
     *
     * @param sessionId 会话ID
     * @return 是否成功
     */
    boolean deleteSession(String sessionId);
}
