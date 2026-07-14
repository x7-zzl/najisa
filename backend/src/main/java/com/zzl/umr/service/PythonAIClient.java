package com.zzl.umr.service;

import com.zzl.umr.model.dto.PythonChatRequest;
import com.zzl.umr.model.dto.PythonChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author zhangzl
 * @date 2026/7/13  17:35
 * @description Python AI 服务的 HTTP 客户端，封装对 FastAPI /ai/chat 的调用
 */
@Slf4j
@Component
public class PythonAIClient {

    @Resource
    private RestTemplate restTemplate;

    /** Python AI 服务地址 */
    @Value("${ai.python.base-url:http://localhost:8089}")
    private String baseUrl;

    /** 聊天接口路径 */
    private static final String CHAT_PATH = "/ai/chat";

    /**
     * 调用 Python AI 服务进行聊天
     *
     * @param request 聊天请求
     * @return AI 响应
     */
    public PythonChatResponse chat(PythonChatRequest request) {
        String url = baseUrl + CHAT_PATH;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PythonChatRequest> entity = new HttpEntity<>(request, headers);

        log.info("调用 Python AI 服务: url={}, message={}", url,
                request.getMessage() != null ? request.getMessage().substring(0, Math.min(50, request.getMessage().length())) : "");

        try {
            ResponseEntity<PythonChatResponse> response = restTemplate.postForEntity(url, entity, PythonChatResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Python AI 响应成功: reply={}",
                        response.getBody().getReply() != null ? response.getBody().getReply().substring(0, Math.min(50, response.getBody().getReply().length())) : "");
                return response.getBody();
            }

            log.error("Python AI 服务返回异常: status={}", response.getStatusCode());
            throw new RuntimeException("AI 服务暂时不可用，请稍后重试");

        } catch (Exception e) {
            log.error("调用 Python AI 服务失败", e);
            // 返回降级响应，避免前端报错
            PythonChatResponse fallback = new PythonChatResponse();
            fallback.setReply("抱歉，我现在有点累了，稍微等一下再聊好吗？💤");
            return fallback;
        }
    }
}
