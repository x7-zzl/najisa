package com.zzl.umr.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangzl
 * @description 通用推送公告消息DTO
 * @date 2026/01/30 14:26:28
 */
@Data
public class SysMessageDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 关联业务标识
    private String businessId;
    // 显示名称
    private String name;
    // 显示名称2
    private String genreName;
    // 操作类型，识别消息目的，add 或 delete
    private String operationType;
    // 时间戳
    private Long timestamp;
}