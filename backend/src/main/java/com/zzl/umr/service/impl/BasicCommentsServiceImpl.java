package com.zzl.umr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.umr.mapper.BasicCommentsMapper;
import com.zzl.umr.mapper.BasicUserMapper;
import com.zzl.umr.model.BasicComments;
import com.zzl.umr.model.BasicUser;
import com.zzl.umr.model.cdn.CommentQryCdn;
import com.zzl.umr.model.dto.HttpResult;
import com.zzl.umr.service.BasicCommentsService;
import com.zzl.umr.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhangzl
 * @description
 * @date 2026/03/16 16:58:54
 */
@Slf4j
@Service("basicCommentsService")
public class BasicCommentsServiceImpl extends ServiceImpl<BasicCommentsMapper, BasicComments> implements BasicCommentsService {


    @Autowired
    private BasicCommentsMapper basicCommentsMapper;

    @Autowired
    private BasicUserMapper basicUserMapper;

    /**
     * 添加评论
     *
     * @param comment 评论信息
     * @return 添加结果
     */
    @Override
    @Transactional
    public boolean add(CommentQryCdn comment) {

        BasicComments dbComment = new BasicComments();

        //把 comment 复制到 dbComment里面
        BeanUtils.copyProperties(comment, dbComment);
        dbComment.setId(SnowflakeIdWorker.newId());

        if (comment.getCommentLevel() != 0) {
            dbComment.setRootCommentId(comment.getParentId());
        }

        return basicCommentsMapper.insert(dbComment) > 0;
    }

    /**
     * 查看所有评论
     *
     * @param comment 评论信息
     * @return 评论列表
     */
    @Override
    public HttpResult queryAllComments(CommentQryCdn comment) {
        if (StringUtils.isEmpty(comment.getBussinessId())) {
            return HttpResult.fail("参数错误");
        }

        LambdaQueryWrapper<BasicComments> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BasicComments::getBussinessId, comment.getBussinessId())
                .orderByAsc(BasicComments::getCreateTime);
        List<BasicComments> allComments = baseMapper.selectList(wrapper);

        if (CollectionUtils.isEmpty(allComments)) {
            return HttpResult.success(Collections.emptyList());
        }

        Set<String> userIds = allComments.stream()
                .map(BasicComments::getUserId)
                .collect(Collectors.toSet());
        // 通过用户服务获取 userId -> userName 的映射
        Map<String, String> userMap = getUserNameMap(userIds);

        // 填充评论者的用户名
        allComments.forEach(c -> c.setUserName(userMap.get(c.getUserId())));

        // 构建 id -> 评论对象的映射，用于快速查找父评论
        Map<String, BasicComments> commentMap = new HashMap<>();
        for (BasicComments c : allComments) {
            commentMap.put(c.getId(), c);
            // 初始化children列表，避免后续空指针
            c.setChildren(new ArrayList<>());
        }

        //  构建评论树并填充被回复人用户名
        List<BasicComments> topComments = new ArrayList<>();
        for (BasicComments c : allComments) {
            String parentId = c.getParentId();
            // 顶层评论的parentId为空字符串
            if (StringUtils.isEmpty(parentId)) {
                topComments.add(c);
            } else {
                // 查找父评论
                BasicComments parent = commentMap.get(parentId);
                if (parent != null) {
                    // 将当前评论添加到父评论的children中
                    parent.getChildren().add(c);
                    // 填充被回复人的用户名（即父评论的发布者）
                    c.setReplyToUserName(parent.getUserName());
                }
            }
        }

        return HttpResult.success(topComments);
    }


    /**
     * 获取用户名
     *
     * @param userIds 用户id
     * @return 用户名
     */
    private Map<String, String> getUserNameMap(Set<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyMap();
        }

        LambdaQueryWrapper<BasicUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.in(BasicUser::getId, userIds);
        List<BasicUser> users = basicUserMapper.selectList(userWrapper);
        return users.stream().collect(Collectors.toMap(BasicUser::getId, BasicUser::getNickName, (a, b) -> a));
    }

}
