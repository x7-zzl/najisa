package com.zzl.umr.model.cdn;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangzl
 * @date 2026/3/16  17:05
 * @description 通用评论查询参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentQryCdn {

    /**
     * 文章或视频ID
     */
    public String bussinessId;

    /**
     * 评论者ID
     */
    public String userId;

    /**
     * 评论内容
     */
    public String comment;

    /**
     * 评论类型（0-文章，1-视频）
     */
    public Integer commentType;

    /**
     * 父级评论ID
     */
    public String parentId;

    /**
     * 评论层级（0-顶层评论，1-回复顶层评论，2-其他）
     */
    public Integer commentLevel;
}
