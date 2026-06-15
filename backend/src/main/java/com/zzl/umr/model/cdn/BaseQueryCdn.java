package com.zzl.umr.model.cdn;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangzl
 * @description 通用分页查询参数
 * @date 2026/01/22 17:36:24
 */
@Data
public class BaseQueryCdn implements Serializable {
    // 当前页码，默认1
    private Integer pageNum = 1;
    // 每页大小，默认8
    private Integer pageSize = 8;
    // 搜索关键字
    private String keyword;

    //类型
    private Integer guType;
    //流派
    private Integer genre;
    //转数
    private Integer guLevel;
}