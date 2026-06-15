package com.zzl.umr.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author zhangzl
 * @description RedisService 类提供了简化的 Redis 操作接口，用于在 Spring Boot 应用中存储和检索数据。
 *        它通过 RedisTemplate 与 Redis 服务器交互，执行常见的操作如设置值、获取值、设置值带过期时间和删除值。
 * @date 2025/11/23 18:01:09
 */
@Service
public class RedisService {

    /*
        意义: RedisTemplate 是 Spring 提供的一个 Redis 操作模板，它抽象了 Redis 的底层访问，
        使开发者可以用 Java 对象操作 Redis。使用 @Autowired 注解，Spring 自动将配置好的 RedisTemplate 注入到 RedisService 类中
     */
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 向 Redis 中存储一个键值对
     * @param key
     * @param value
     */
    public void setValue(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 从 Redis 中获取指定键的值
     * @param key
     * @return
     */
    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 向 Redis 中存储一个键值对，并设置其过期时间
     * @param key
     * @param value
     * @param timeout 时间量
     * @param timeUnit 时间单位
     */
    public void setValueWithExpiry(String key, Object value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 为指定 key 设置过期时间
     * @param key      Redis 键
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     * @return 如果设置成功，则返回 true；否则返回 false
     */
    public boolean expire(String key, long timeout, TimeUnit timeUnit) {
        return redisTemplate.expire(key, timeout, timeUnit);
    }

    /**
     * 从 Redis 中删除指定键及其对应的值
     * @param key
     */
    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 判断 key 是否存在
     * @param key
     * @return
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }


    /**
     * 原子递增指定 key 的值（如果 key 不存在，则初始化为 0 再递增）
     * @param key   Redis 键
     * @param delta 增量（必须为正数，通常为 1）
     * @return 递增后的值
     */
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }


}
