package com.zzl.umr.model.cdn;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangzl
 * @description 文章浏览次数统计
 * @date 2026/03/16 10:37:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleViewCdn {
    // 文章id
    private String articleId;
    // 用户id
    private String userId;

}
