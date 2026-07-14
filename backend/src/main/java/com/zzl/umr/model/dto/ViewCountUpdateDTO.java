package com.zzl.umr.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * @author zhangzl
 * @date 2026/3/16  14:51
 * @description 浏览量更新消息
 */
@Data
public class ViewCountUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    private String articleId;

    /**
     * 递增次数，默认为1
     */
    private int increment = 1;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 用户ID
     */
    private String userId;
}