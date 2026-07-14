package com.zzl.umr.model.cdn;

import lombok.Data;

/**
 * @author zhangzl
 * @date 2026/7/13  17:35
 * @description 前端发送消息的请求参数
 */
@Data
public class SendMessageCdn {

    /**
     * 消息内容
     */
    private String message;

    /**
     * 会话ID（为空则新建会话）
     */
    private String sessionId;

    /**
     * 角色ID（为空则使用默认角色）
     */
    private String characterId;
}
