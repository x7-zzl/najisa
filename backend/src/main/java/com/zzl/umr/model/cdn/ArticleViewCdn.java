package com.zzl.umr.model.cdn;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangzl
 * @date 2026/3/16  10:37
 * @description 文章浏览次数统计
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleViewCdn {

    /**
     * 文章ID
     */
    private String articleId;

    /**
     * 用户ID
     */
    private String userId;

}
