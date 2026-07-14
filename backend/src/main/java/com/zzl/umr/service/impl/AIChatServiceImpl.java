package com.zzl.umr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zzl.umr.mapper.AiChatMessageMapper;
import com.zzl.umr.mapper.AiChatSessionMapper;
import com.zzl.umr.mapper.BasicUserMapper;
import com.zzl.umr.model.AiCharacter;
import com.zzl.umr.model.AiChatMessage;
import com.zzl.umr.model.AiChatSession;
import com.zzl.umr.model.BasicUser;
import com.zzl.umr.model.cdn.SendMessageCdn;
import com.zzl.umr.model.dto.ChatSessionVO;
import com.zzl.umr.model.dto.PythonChatRequest;
import com.zzl.umr.model.dto.PythonChatResponse;
import com.zzl.umr.model.dto.SendMessageVO;
import com.zzl.umr.service.AIChatService;
import com.zzl.umr.service.AiCharacterService;
import com.zzl.umr.service.AiEmotionService;
import com.zzl.umr.service.AiMemoryService;
import com.zzl.umr.service.AiRelationshipService;
import com.zzl.umr.service.PythonAIClient;
import com.zzl.umr.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhangzl
 * @date 2026/7/13  17:35
 * @description AI聊天核心服务实现类，编排整个聊天流程
 */
@Slf4j
@Service("aiChatService")
public class AIChatServiceImpl implements AIChatService {

    private static final String DEFAULT_CHARACTER_ID = "zhangyuhan";

    @Resource
    private AiChatSessionMapper aiChatSessionMapper;

    @Resource
    private AiChatMessageMapper aiChatMessageMapper;

    @Resource
    private BasicUserMapper basicUserMapper;

    @Resource
    private PythonAIClient pythonAIClient;

    @Resource
    private AiCharacterService aiCharacterService;

    @Resource
    private AiEmotionService aiEmotionService;

    @Resource
    private AiRelationshipService aiRelationshipService;

    @Resource
    private AiMemoryService aiMemoryService;

    /**
     * 发送消息，核心流程：保存消息、调用Python、更新情绪关系
     *
     * @param cdn 请求参数
     * @return 发送结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public SendMessageVO sendMessage(SendMessageCdn cdn) {
        String userId = getCurrentUserId();
        String characterId = resolveCharacterId(cdn.getCharacterId());

        AiChatSession session = getOrCreateSession(cdn.getSessionId(), userId, characterId);

        AiChatMessage userMsg = new AiChatMessage();
        userMsg.setId(SnowflakeIdWorker.newId());
        userMsg.setSessionId(session.getId());
        userMsg.setRole("user");
        userMsg.setContent(cdn.getMessage());
        userMsg.setCreateTime(new Date());
        aiChatMessageMapper.insert(userMsg);

        Map<String, Integer> emotionSnapshot = aiEmotionService.getEmotionSnapshot(userId, characterId);
        Map<String, Object> relationshipSnapshot = aiRelationshipService.getRelationshipSnapshot(userId, characterId);
        List<PythonChatRequest.HistoryMessage> history = loadRecentHistory(session.getId(), 20);
        List<String> memorySummaries = aiMemoryService.getMemorySummaries(userId, characterId);

        PythonChatRequest pyRequest = PythonChatRequest.builder()
                .message(cdn.getMessage())
                .userId(userId)
                .characterId(characterId)
                .chatId(session.getId())
                .history(history)
                .emotion(emotionSnapshot)
                .relationship(relationshipSnapshot)
                .memories(memorySummaries)
                .build();

        PythonChatResponse pyResponse = pythonAIClient.chat(pyRequest);

        AiChatMessage aiMsg = new AiChatMessage();
        aiMsg.setId(SnowflakeIdWorker.newId());
        aiMsg.setSessionId(session.getId());
        aiMsg.setRole("assistant");
        aiMsg.setContent(pyResponse.getReply());
        aiMsg.setTokenCount(pyResponse.getUsage() != null ? pyResponse.getUsage().get("promptTokens") : null);
        aiMsg.setCreateTime(new Date());
        aiChatMessageMapper.insert(aiMsg);

        session.setLastMessage(cdn.getMessage().length() > 50 ? cdn.getMessage().substring(0, 50) : cdn.getMessage());
        session.setMessageCount((session.getMessageCount() != null ? session.getMessageCount() : 0) + 2);
        session.setUpdateTime(new Date());
        aiChatSessionMapper.updateById(session);

        aiEmotionService.updateAfterChat(userId, characterId, cdn.getMessage());
        aiRelationshipService.updateAfterChat(userId, characterId);

        return SendMessageVO.builder()
                .sessionId(session.getId())
                .reply(pyResponse.getReply())
                .emotion(emotionSnapshot)
                .relationship(relationshipSnapshot)
                .usage(pyResponse.getUsage())
                .build();
    }

    /**
     * 获取当前用户的会话列表
     *
     * @return 会话列表
     */
    @Override
    public List<ChatSessionVO> getMySessions() {
        String userId = getCurrentUserId();

        LambdaQueryWrapper<AiChatSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatSession::getUserId, userId);
        wrapper.eq(AiChatSession::getIsActive, 1);
        wrapper.orderByDesc(AiChatSession::getUpdateTime);
        List<AiChatSession> sessions = aiChatSessionMapper.selectList(wrapper);

        return sessions.stream().map(session -> {
            AiCharacter character = aiCharacterService.queryById(session.getCharacterId());
            return ChatSessionVO.builder()
                    .chatId(session.getId())
                    .characterId(session.getCharacterId())
                    .characterName(character != null ? character.getName() : "未知")
                    .characterAvatar(character != null ? character.getAvatarUrl() : null)
                    .title(session.getTitle())
                    .lastMessage(session.getLastMessage())
                    .messageCount(session.getMessageCount())
                    .updateTime(session.getUpdateTime())
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 获取指定会话的消息列表
     *
     * @param sessionId 会话ID
     * @return 消息列表
     */
    @Override
    public List<AiChatMessage> getSessionMessages(String sessionId) {
        LambdaQueryWrapper<AiChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatMessage::getSessionId, sessionId);
        wrapper.orderByAsc(AiChatMessage::getCreateTime);
        return aiChatMessageMapper.selectList(wrapper);
    }

    /**
     * 新建会话
     *
     * @param characterId 角色ID
     * @return 新会话ID
     */
    @Override
    public String createSession(String characterId) {
        String userId = getCurrentUserId();
        String cid = resolveCharacterId(characterId);

        AiChatSession session = new AiChatSession();
        session.setId(SnowflakeIdWorker.newId());
        session.setUserId(userId);
        session.setCharacterId(cid);
        session.setTitle("新的对话");
        session.setMessageCount(0);
        session.setIsActive(1);
        session.setCreateTime(new Date());
        session.setUpdateTime(new Date());
        aiChatSessionMapper.insert(session);

        return session.getId();
    }

    /**
     * 删除会话，软删除
     *
     * @param sessionId 会话ID
     * @return 是否成功
     */
    @Override
    public boolean deleteSession(String sessionId) {
        AiChatSession session = aiChatSessionMapper.selectById(sessionId);
        if (session == null) {
            return false;
        }

        String userId = getCurrentUserId();
        if (!userId.equals(session.getUserId())) {
            log.warn("用户 {} 试图删除不属于自己的会话 {}", userId, sessionId);
            return false;
        }

        session.setIsActive(0);
        session.setUpdateTime(new Date());
        return aiChatSessionMapper.updateById(session) > 0;
    }

    /**
     * 从SecurityContext获取当前登录用户的ID
     *
     * @return 用户ID
     */
    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("用户未登录");
        }
        String userName = auth.getName();

        LambdaQueryWrapper<BasicUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BasicUser::getUserName, userName);
        wrapper.last("limit 1");
        BasicUser user = basicUserMapper.selectOne(wrapper);

        if (user == null) {
            throw new RuntimeException("用户不存在: " + userName);
        }
        return user.getId();
    }

    /**
     * 解析角色ID，为空时使用默认角色
     *
     * @param characterId 角色ID
     * @return 角色ID
     */
    private String resolveCharacterId(String characterId) {
        if (characterId != null && !characterId.isEmpty()) {
            return characterId;
        }
        return DEFAULT_CHARACTER_ID;
    }

    /**
     * 获取或创建会话
     *
     * @param sessionId   会话ID
     * @param userId      用户ID
     * @param characterId 角色ID
     * @return 会话
     */
    private AiChatSession getOrCreateSession(String sessionId, String userId, String characterId) {
        if (sessionId != null && !sessionId.isEmpty()) {
            AiChatSession session = aiChatSessionMapper.selectById(sessionId);
            if (session != null && userId.equals(session.getUserId())) {
                return session;
            }
        }

        AiChatSession session = new AiChatSession();
        session.setId(SnowflakeIdWorker.newId());
        session.setUserId(userId);
        session.setCharacterId(characterId);
        session.setTitle("新的对话");
        session.setMessageCount(0);
        session.setIsActive(1);
        session.setCreateTime(new Date());
        session.setUpdateTime(new Date());
        aiChatSessionMapper.insert(session);
        return session;
    }

    /**
     * 加载会话的最近N条消息作为上下文
     *
     * @param sessionId 会话ID
     * @param limit     消息数量
     * @return 历史消息列表
     */
    private List<PythonChatRequest.HistoryMessage> loadRecentHistory(String sessionId, int limit) {
        LambdaQueryWrapper<AiChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatMessage::getSessionId, sessionId);
        wrapper.orderByDesc(AiChatMessage::getCreateTime);
        wrapper.last("limit " + limit);
        List<AiChatMessage> messages = aiChatMessageMapper.selectList(wrapper);

        List<AiChatMessage> reversed = new ArrayList<>(messages);
        java.util.Collections.reverse(reversed);

        return reversed.stream()
                .map(m -> PythonChatRequest.HistoryMessage.builder()
                        .role(m.getRole())
                        .content(m.getContent())
                        .build())
                .collect(Collectors.toList());
    }
}
