package com.zzl.umr.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzl.umr.model.BasicArticle;
import com.zzl.umr.model.cdn.ArticleViewCdn;
import com.zzl.umr.model.cdn.BaseQueryCdn;
import com.zzl.umr.model.dto.HttpResult;
import com.zzl.umr.service.BasicArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("basicArticle")
@Api(tags = "文章管理")
@RequiredArgsConstructor
public class BasicArticleController {

    private final BasicArticleService basicArticleService;

    @PostMapping("/queryListPage")
    @ApiOperation("分页查询文章列表")
    public HttpResult<Page<BasicArticle>> queryListPage(@RequestBody BaseQueryCdn queryCdn) {
        return HttpResult.success(basicArticleService.queryListPage(queryCdn));
    }

    @PostMapping("/add")
    @ApiOperation("发布文章")
    public HttpResult add(@RequestBody BasicArticle basicArticle) {
        if (basicArticleService.add(basicArticle)) {
            return HttpResult.success("发布成功");
        }
        return HttpResult.fail();
    }

    @GetMapping("/queryById")
    @ApiOperation("查询文章详情")
    public HttpResult queryById(String id) {
        return basicArticleService.queryById(id);
    }

    //增加每篇文章浏览次数
    @PostMapping("/addViewCount")
    @ApiOperation("文章浏览次数")
    public HttpResult addViewCount(@RequestBody ArticleViewCdn basicArticle) {
        return basicArticleService.addViewCount(basicArticle);
    }
}
