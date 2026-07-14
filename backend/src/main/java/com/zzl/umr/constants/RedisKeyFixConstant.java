package com.zzl.umr.constants;

import lombok.Data;

/**
 * @author zhangzl
 * @description Redis Key 前缀常量类
 * @date 2026/03/16 11:29:20
 */
@Data
public class RedisKeyFixConstant {
    // 用户登录令牌
    public static final String USER_LOGIN_TOKEN_KEY_FIX = "jwt_blacklist:";

    // 文章浏览量
    public static final String VIEW_COUNT_KEY_PREFIX = "article:view:";

    // 每日浏览量前缀
    public static final String DAILY_VIEW_KEY_PREFIX = "article:daily:view:";

    // 聊天上下文缓存前缀（暂未使用，预留给后续 Redis 缓存聊天上下文）
    public static final String CHAT_CONTEXT_KEY_PREFIX = "chat:context:";

    // 聊天消息历史缓存前缀（最近N条消息）
    public static final String CHAT_HISTORY_KEY_PREFIX = "chat:history:";


}
