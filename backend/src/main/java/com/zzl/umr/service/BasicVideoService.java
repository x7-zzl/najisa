package com.zzl.umr.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.umr.model.BasicVideo;
import com.zzl.umr.model.cdn.BaseQueryCdn;
import com.zzl.umr.model.dto.HttpResult;

public interface BasicVideoService extends IService<BasicVideo> {
    Page<BasicVideo> queryListPage(BaseQueryCdn queryCdn);

    boolean add(BasicVideo basicVideo);

    HttpResult queryById(String id);
}

