package com.zzl.umr.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author zhangzl
 * @description 本地文件访问配置，Spring 静态资源映射
 * @date 2026/01/16 15:21:23
 */
@Configuration
public class LocalFileWebConfig implements WebMvcConfigurer {

    @Value("${file.storage.local-dir:./uploads}")
    private String localDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 转为绝对路径，Windows/Linux 通用
        Path uploadPath = Paths.get(localDir).toAbsolutePath().normalize();
        String location = uploadPath.toUri().toString();

        // 浏览器访问：http://host:port/files/2026/01/16/xxx.png
        registry.addResourceHandler("/files/**")
                .addResourceLocations(location)
                .setCachePeriod(3600);

    }
}
