package com.zzl.umr.controller;

import com.zzl.umr.model.AiCharacter;
import com.zzl.umr.model.dto.HttpResult;
import com.zzl.umr.service.AiCharacterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhangzl
 * @date 2026/7/13  17:35
 * @description AI角色，查询可用角色
 */
@RestController
@RequestMapping("aiCharacter")
@Api(tags = "AI角色管理")
@RequiredArgsConstructor
public class AiCharacterController {

    private final AiCharacterService aiCharacterService;

    @GetMapping("/list")
    @ApiOperation("获取可用角色列表")
    public HttpResult list() {
        List<AiCharacter> list = aiCharacterService.queryEnabledList();
        return HttpResult.success(list);
    }

    @GetMapping("/{id}")
    @ApiOperation("查询角色详情")
    public HttpResult queryById(@PathVariable String id) {
        AiCharacter character = aiCharacterService.queryById(id);
        if (character == null) {
            return HttpResult.fail("角色不存在");
        }
        return HttpResult.success(character);
    }
}
