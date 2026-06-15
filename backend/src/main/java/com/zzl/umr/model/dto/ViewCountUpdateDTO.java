package com.zzl.umr.model.dto;

import lombok.Data;

import java.io.Serializable;


/**
 * @author zhangzl
 * @description 浏览量更新消息
 * @date 2026/03/16 14:51:09
 */
@Data
public class ViewCountUpdateDTO implements Serializable {
    private String articleId;
    // 递增次数，默认为1
    private int increment = 1;
    private Long timestamp;
    private String userId;
}