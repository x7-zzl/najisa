package com.zzl.umr.controller;

import com.zzl.umr.model.BasicUserLogin;
import com.zzl.umr.model.dto.HttpResult;
import com.zzl.umr.service.BasicUserLoginService;
import com.zzl.umr.service.RedisService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static com.zzl.umr.constants.MessageConstant.*;

/**
 * (BasicUserLogin)表控制层
 *
 * @author zhangzl
 * @since 2025-10-20 22:09:48
 */
@RestController
@RequestMapping("basicUserLogin")
@Api(tags = "登录信息管理")
public class BasicUserLoginController {
    /**
     * 服务对象
     */
    @Resource
    private BasicUserLoginService basicUserLoginService;

    @Resource
    private RedisService redisService;


    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/queryById")
    public HttpResult queryById(String id) {
        return HttpResult.success(basicUserLoginService.queryById(id));
    }

    /**
     * 新增数据
     *
     * @param basicUserLogin 实体
     * @return 新增结果
     */
    @PostMapping("/add")
    public HttpResult add(@RequestBody BasicUserLogin basicUserLogin) {
        if (basicUserLoginService.add(basicUserLogin)) {
            return HttpResult.success(ADD_SUCCESS_MSG);
        }
        return HttpResult.fail();
    }

    /**
     * 编辑数据
     *
     * @param basicUserLogin 实体
     * @return 编辑结果
     */
    @PostMapping("/update")
    public HttpResult update(@RequestBody BasicUserLogin basicUserLogin) {
        if (basicUserLoginService.updateById(basicUserLogin)) {
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
        if (basicUserLoginService.deleteById(id)) {
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
        if (basicUserLoginService.batchDelete(idList) > 0) {
            return HttpResult.success(DELETE_SUCCESS_MSG);
        }
        return HttpResult.fail();
    }
}
