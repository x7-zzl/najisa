package com.zzl.umr.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author zhangzl
 * @date 2026/1/30  14:26
 * @description 通用推送公告消息DTO
 */
@Data
public class SysMessageDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 关联业务标识
     */
    private String businessId;

    /**
     * 显示名称
     */
    private String name;

    /**
     * 显示名称2
     */
    private String genreName;

    /**
     * 操作类型，识别消息目的（add / delete）
     */
    private String operationType;

    /**
     * 时间戳
     */
    private Long timestamp;
}