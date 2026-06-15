package com.zzl.umr.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author zhangzl
 * @description
 * @date 2025/12/06 23:42:52
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        // 配置跨域信息
        CorsConfiguration config = new CorsConfiguration();
        // 允许前端域名
        config.addAllowedOrigin("*");
        // 允许所有请求头
        config.addAllowedHeader("*");
        // 允许所有请求方法（GET/POST/PUT/DELETE/OPTIONS）
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}