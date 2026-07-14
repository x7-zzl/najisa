package com.zzl.umr.controller;

import com.zzl.umr.model.AiChatMessage;
import com.zzl.umr.model.cdn.SendMessageCdn;
import com.zzl.umr.model.dto.ChatSessionVO;
import com.zzl.umr.model.dto.HttpResult;
import com.zzl.umr.model.dto.SendMessageVO;
import com.zzl.umr.service.AIChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhangzl
 * @date 2026/7/13  17:35
 * @description AI聊天，发送消息、管理会话
 */
@RestController
@RequestMapping("aiChat")
@Api(tags = "AI聊天管理")
@RequiredArgsConstructor
public class AIChatController {

    private final AIChatService aiChatService;

    @PostMapping("/send")
    @ApiOperation("发送消息给AI")
    public HttpResult<SendMessageVO> sendMessage(@RequestBody SendMessageCdn cdn) {
        SendMessageVO result = aiChatService.sendMessage(cdn);
        return HttpResult.success(result);
    }

    @GetMapping("/sessions")
    @ApiOperation("获取我的聊天会话列表")
    public HttpResult<List<ChatSessionVO>> getMySessions() {
        List<ChatSessionVO> sessions = aiChatService.getMySessions();
        return HttpResult.success(sessions);
    }

    @GetMapping("/sessions/{sessionId}/messages")
    @ApiOperation("获取会话消息列表")
    public HttpResult<List<AiChatMessage>> getSessionMessages(@PathVariable String sessionId) {
        List<AiChatMessage> messages = aiChatService.getSessionMessages(sessionId);
        return HttpResult.success(messages);
    }

    @PostMapping("/session/new")
    @ApiOperation("新建聊天会话")
    public HttpResult<String> createSession(@RequestBody(required = false) SendMessageCdn cdn) {
        String characterId = cdn != null ? cdn.getCharacterId() : null;
        String sessionId = aiChatService.createSession(characterId);
        return HttpResult.success(sessionId);
    }

    @DeleteMapping("/session/{sessionId}")
    @ApiOperation("删除聊天会话")
    public HttpResult<String> deleteSession(@PathVariable String sessionId) {
        if (aiChatService.deleteSession(sessionId)) {
            return HttpResult.success("删除成功");
        }
        return HttpResult.fail("删除失败");
    }
}
