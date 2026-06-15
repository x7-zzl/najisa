package com.zzl.umr.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.umr.model.BasicComments;
import com.zzl.umr.model.cdn.CommentQryCdn;
import com.zzl.umr.model.dto.HttpResult;

/**
 * @author zhangzl
 * @description
 * @date 2026/03/16 16:58:10
 */
public interface BasicCommentsService extends IService<BasicComments> {

    /**
     * 发布评论
     *
     * @param comment
     * @return
     */
    boolean add(CommentQryCdn comment);

    /**
     * 查看所有评论
     *
     * @param comment
     * @return
     */
    HttpResult queryAllComments(CommentQryCdn comment);


}
