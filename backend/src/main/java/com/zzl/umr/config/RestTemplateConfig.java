package com.zzl.umr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author zhangzl
 * @date 2026/7/13  17:35
 * @description RestTemplate 配置，用于调用 Python AI 服务
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // 连接超时 10 秒
        factory.setConnectTimeout(10_000);
        // 读取超时 60 秒（AI 回复可能较慢）
        factory.setReadTimeout(60_000);
        return new RestTemplate(factory);
    }
}
