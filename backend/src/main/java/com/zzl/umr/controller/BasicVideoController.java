package com.zzl.umr.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzl.umr.model.BasicVideo;
import com.zzl.umr.model.cdn.BaseQueryCdn;
import com.zzl.umr.model.dto.HttpResult;
import com.zzl.umr.service.BasicVideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("basicVideo")
@Api(tags = "视频管理")
@RequiredArgsConstructor
public class BasicVideoController {

    private final BasicVideoService basicVideoService;

    @PostMapping("/queryListPage")
    @ApiOperation("分页查询视频列表")
    public HttpResult<Page<BasicVideo>> queryListPage(@RequestBody BaseQueryCdn queryCdn) {
        return HttpResult.success(basicVideoService.queryListPage(queryCdn));
    }

    @PostMapping("/add")
    @ApiOperation("发布视频")
    public HttpResult add(@RequestBody BasicVideo basicVideo) {
        if (basicVideoService.add(basicVideo)) {
            return HttpResult.success("发布成功");
        }
        return HttpResult.fail();
    }

    @GetMapping("/queryById")
    @ApiOperation("查询视频详情")
    public HttpResult queryById(String id) {
        return basicVideoService.queryById(id);
    }
}

