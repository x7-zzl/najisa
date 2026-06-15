package com.zzl.umr.controller;

import com.zzl.umr.model.cdn.CommentQryCdn;
import com.zzl.umr.model.dto.HttpResult;
import com.zzl.umr.service.BasicCommentsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author zhangzl
 * @description 评论模块
 * @date 2026/03/16 17:01:54
 */

@RestController
@RequestMapping("basicComments")
@Api(tags = "评论管理")
public class BasicCommentsController {

    @Resource
    private BasicCommentsService basicCommentsService;


    @PostMapping("/add")
    @ApiOperation("回复评论")
    public HttpResult add(@RequestBody CommentQryCdn comment) {
        if (basicCommentsService.add(comment)) {
            return HttpResult.success("评论发布成功");
        }
        return HttpResult.fail();
    }


    @ApiOperation("查看所有评论")
    @PostMapping("/queryAllComments")
    public HttpResult queryAllComments(@RequestBody CommentQryCdn comment) {

        return basicCommentsService.queryAllComments(comment);
    }


}
