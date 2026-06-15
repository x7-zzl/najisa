package com.zzl.umr;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zhangzl
 * @description 启动类
 * @date 2025/7/7 07:07
 */
@MapperScan("com.zzl.umr.mapper")
@SpringBootApplication
public class NajisaApplication {

    public static void main(String[] args) {
        SpringApplication.run(NajisaApplication.class, args);
    }

}
