package com.zzl.umr.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzl.umr.enums.GuInsectGenreEnum;
import com.zzl.umr.enums.GuInsectLevelEnum;
import com.zzl.umr.enums.GuTypeEnum;
import com.zzl.umr.model.BasicGuInsect;
import com.zzl.umr.model.cdn.BaseQueryCdn;
import com.zzl.umr.model.dto.HttpResult;
import com.zzl.umr.service.BasicGuInsectService;
import com.zzl.umr.utils.EnumUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.zzl.umr.constants.MessageConstant.ADD_SUCCESS_MSG;
import static com.zzl.umr.constants.MessageConstant.DELETE_SUCCESS_MSG;
import static com.zzl.umr.constants.MessageConstant.ENUM_GET_CODE_METHOD_NAME;
import static com.zzl.umr.constants.MessageConstant.ENUM_GET_NAME_METHOD_NAME;
import static com.zzl.umr.constants.MessageConstant.UPDATE_SUCCESS_MSG;

/**
 * 蛊虫信息表(BasicGuInsect)表控制层
 *
 * @author zhangzl
 * @since 2026-01-22 17:04:03
 */
@RestController
@RequestMapping("basicGuInsect")
@Api(tags = "蛊虫信息管理")
public class BasicGuInsectController {
    /**
     * 服务对象
     */
    @Resource
    private BasicGuInsectService basicGuInsectService;


    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/queryById")
    public HttpResult queryById(String id) {
        return HttpResult.success(basicGuInsectService.queryById(id));
    }

    /**
     * 新增数据
     *
     * @param basicGuInsect 实体
     * @return 新增结果
     */
    @PostMapping("/add")
    public HttpResult add(@RequestBody BasicGuInsect basicGuInsect) {
        if (basicGuInsectService.add(basicGuInsect)) {
            return HttpResult.success(ADD_SUCCESS_MSG);
        }
        return HttpResult.fail();
    }

    /**
     * 编辑数据
     *
     * @param basicGuInsect 实体
     * @return 编辑结果
     */
    @PostMapping("/update")
    public HttpResult update(@RequestBody BasicGuInsect basicGuInsect) {
        if (basicGuInsectService.updateById(basicGuInsect)) {
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
        if (basicGuInsectService.deleteById(id)) {
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
        if (basicGuInsectService.batchDelete(idList) > 0) {
            return HttpResult.success(DELETE_SUCCESS_MSG);
        }
        return HttpResult.fail();
    }


    /**
     * 分页查询蛊虫列表
     *
     * @param queryCdn 查询参数(页码, 页大小, 关键词)
     */
    @PostMapping("/queryListPage")
    @ApiOperation("分页查询蛊虫列表")
    public HttpResult<Page<BasicGuInsect>> queryListPage(@RequestBody BaseQueryCdn queryCdn) {
        return HttpResult.success(basicGuInsectService.queryListPage(queryCdn));
    }

    /**
     * 获取所有枚举选项
     */
    @GetMapping("/getEnumOptions")
    @ApiOperation("获取所有枚举选项")
    public HttpResult<Map<String, List<EnumUtil.EnumOption>>> getEnumOptions() {
        // 获取蛊虫类别枚举
        List<EnumUtil.EnumOption> guTypeOptions = EnumUtil.getEnumOptions(
                GuTypeEnum.class, ENUM_GET_CODE_METHOD_NAME, ENUM_GET_NAME_METHOD_NAME
        );

        // 获取蛊虫流派枚举
        List<EnumUtil.EnumOption> genreOptions = EnumUtil.getEnumOptions(
                GuInsectGenreEnum.class, ENUM_GET_CODE_METHOD_NAME, ENUM_GET_NAME_METHOD_NAME
        );

        // 获取蛊虫转数枚举
        List<EnumUtil.EnumOption> guLevelOptions = EnumUtil.getEnumOptions(
                GuInsectLevelEnum.class, ENUM_GET_CODE_METHOD_NAME, ENUM_GET_NAME_METHOD_NAME
        );

        // 移除"不明"选项（code为0）
        genreOptions = genreOptions.stream()
                .filter(option -> !Integer.valueOf(0).equals(option.getValue()))
                .collect(Collectors.toList());

        guLevelOptions = guLevelOptions.stream()
                .filter(option -> !Integer.valueOf(0).equals(option.getValue()))
                .collect(Collectors.toList());

        Map<String, List<EnumUtil.EnumOption>> result = new HashMap<>();
        result.put("guType", guTypeOptions);
        result.put("genre", genreOptions);
        result.put("guLevel", guLevelOptions);

        return HttpResult.success(result);
    }

}
