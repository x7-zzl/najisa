package com.zzl.umr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzl.umr.model.BasicComments;
import com.zzl.umr.model.cdn.CommentQryCdn;

import java.util.List;
import java.util.Map;

/**
 * @author zhangzl
 * @description 评论信息表(BasicComments)表数据库访问层
 * @date 2025/10/14 23:52
 */
public interface BasicCommentsMapper extends BaseMapper<BasicComments> {

    /**
     * 查看所有评论
     *
     * @param comment
     * @return
     */
    List<Map<String, List<BasicComments>>> queryAllComments(CommentQryCdn comment);
}