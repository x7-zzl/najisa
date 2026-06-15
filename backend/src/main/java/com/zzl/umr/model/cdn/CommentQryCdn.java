package com.zzl.umr.model.cdn;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangzl
 * @description 通用评论查询参数
 * @date 2026/03/16 17:05:48
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentQryCdn {

    // 文章或者视频id
    public String bussinessId;

    // 评论者id
    public String userId;

    // 评论内容
    public String comment;

    // 评论类型（0文章1视频）
    public Integer commentType;

    // 父级评论id
    public String parentId;

    // 评论层级（顶层评论0，回复顶层评论1，其他2）
    public Integer commentLevel;
}
