package com.zzl.umr.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.umr.model.BasicArticle;
import com.zzl.umr.model.cdn.ArticleViewCdn;
import com.zzl.umr.model.cdn.BaseQueryCdn;
import com.zzl.umr.model.dto.HttpResult;

public interface BasicArticleService extends IService<BasicArticle> {
    Page<BasicArticle> queryListPage(BaseQueryCdn queryCdn);

    boolean add(BasicArticle basicArticle);

    HttpResult queryById(String id);

    HttpResult addViewCount(ArticleViewCdn basicArticle);
}
