package com.zzl.umr.controller;

import com.zzl.umr.model.BasicUserWealth;
import com.zzl.umr.model.dto.HttpResult;
import com.zzl.umr.service.BasicUserWealthService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static com.zzl.umr.constants.MessageConstant.*;

/**
 * 用户财富信息表(BasicUserWealth)表控制层
 *
 * @author zhangzl
 * @since 2026-01-16 10:41:47
 */
@RestController
@RequestMapping("basicUserWealth")
@Api(tags = "财富信息管理")
public class BasicUserWealthController {
    /**
     * 服务对象
     */
    @Resource
    private BasicUserWealthService basicUserWealthService;


    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/queryById")
    public HttpResult queryById(String id) {
        return HttpResult.success(basicUserWealthService.queryById(id));
    }

    /**
     * 新增数据
     *
     * @param basicUserWealth 实体
     * @return 新增结果
     */
    @PostMapping("/add")
    public HttpResult add(@RequestBody BasicUserWealth basicUserWealth) {
        if (basicUserWealthService.add(basicUserWealth)) {
            return HttpResult.success(ADD_SUCCESS_MSG);
        }
        return HttpResult.fail();
    }

    /**
     * 编辑数据
     *
     * @param basicUserWealth 实体
     * @return 编辑结果
     */
    @PostMapping("/update")
    public HttpResult update(@RequestBody BasicUserWealth basicUserWealth) {
        if (basicUserWealthService.updateById(basicUserWealth)) {
            return HttpResult.success(UPDATE_SUCCESS_MSG);
        }
        return HttpResult.fail();
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @PostMapping("/deleteById")
    public HttpResult deleteById(String id) {
        if (basicUserWealthService.deleteById(id)) {
            return HttpResult.success(DELETE_SUCCESS_MSG);
        }
        return HttpResult.fail();
    }

    /**
     * 批量删除数据
     *
     * @param idList 主键列表
     * @return 删除是否成功
     */
    @PostMapping("/batchDelete")
    public HttpResult batchDelete(@RequestBody List<String> idList) {
        if (basicUserWealthService.batchDelete(idList) > 0) {
            return HttpResult.success(DELETE_SUCCESS_MSG);
        }
        return HttpResult.fail();
    }
}
