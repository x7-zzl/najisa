package com.zzl.umr.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzl.umr.model.BasicUser;
import com.zzl.umr.model.cdn.BaseQueryCdn;
import com.zzl.umr.model.cdn.LoginCdn;
import com.zzl.umr.model.dto.HttpResult;
import com.zzl.umr.model.dto.UserProfileDTO;
import com.zzl.umr.service.BasicUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

import static com.zzl.umr.constants.MessageConstant.ADD_SUCCESS_MSG;
import static com.zzl.umr.constants.MessageConstant.DELETE_SUCCESS_MSG;
import static com.zzl.umr.constants.MessageConstant.UPDATE_SUCCESS_MSG;

/**
 * 用户信息表(BasicUser)表控制层
 *
 * @author zhangzl
 * @since 2025-10-15 00:26:41
 */
@RestController
@RequestMapping("basicUser")
@Api(tags = "用户信息管理")
@RequiredArgsConstructor
public class BasicUserController {
    /**
     * 服务对象
     */
    private final BasicUserService basicUserService;
    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/queryById")
    public HttpResult queryById(String id) {
        return HttpResult.success(basicUserService.queryById(id));
    }

    /**
     * 新增数据
     *
     * @param basicUser 实体
     * @return 新增结果
     */
    @PostMapping("/add")
    public HttpResult add(@RequestBody BasicUser basicUser) {

        if (basicUserService.add(basicUser)) {
            return HttpResult.success(ADD_SUCCESS_MSG);
        }

        return HttpResult.fail();
    }

    /**
     * 编辑数据
     *
     * @param basicUser 实体
     * @return 编辑结果
     */
    @PostMapping("/update")
    public HttpResult update(@RequestBody BasicUser basicUser) {
        if (basicUserService.updateById(basicUser)) {
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
        if (basicUserService.deleteById(id)) {
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
        if (basicUserService.batchDelete(idList) > 0) {
            return HttpResult.success(DELETE_SUCCESS_MSG);
        }
        return HttpResult.fail();
    }

    /**
     * 登录
     *
     * @param logInCdn 登录接口参数
     * @return 登录是否成功
     */
    @PostMapping("/login")
    @ApiOperation("登录")
    public HttpResult login(@RequestBody LoginCdn logInCdn) {
        return basicUserService.login(logInCdn);
    }

    /**
     * 退出登录
     *
     * @return 退出登录是否成功
     */
    @PostMapping("/logout")
    @ApiOperation("退出登录")
    public HttpResult logout() {
        return basicUserService.logout();
    }


    /**
     * 注册
     *
     * @param logInCdn 注册接口参数
     * @return 注册是否成功
     */
    @PostMapping("/register")
    @ApiOperation("注册")
    public HttpResult<String> register(@RequestBody LoginCdn logInCdn) {
        return basicUserService.register(logInCdn);
    }


    /**
     * 忘记密码
     *
     * @param logInCdn 忘记密码接口参数
     * @return 是否成功
     */
    @PostMapping("/forgetPassword")
    @ApiOperation("忘记密码")
    public HttpResult<String> forgetPassword(@RequestBody LoginCdn logInCdn) {
        return basicUserService.forgetPassword(logInCdn);
    }

    @PostMapping("/isAdminRole")
    @ApiOperation("用户是否是管理员")
    public HttpResult<String> isAdminRole(@RequestBody LoginCdn logInCdn) {

        if (logInCdn.getUserName().equals("admin")) {
            return HttpResult.success("true");
        }
        return HttpResult.success("false");
    }

    /**
     * 分页查询用户列表
     *
     * @param queryCdn 查询参数(页码, 页大小, 关键词)
     */
    @PostMapping("/queryListPage")
    @ApiOperation("分页查询用户列表")
    public HttpResult<Page<BasicUser>> queryListPage(@RequestBody BaseQueryCdn queryCdn) {
        return HttpResult.success(basicUserService.queryListPage(queryCdn));
    }

    @GetMapping("/getUserProfile")
    @ApiOperation("获取用户个人信息")
    public HttpResult<UserProfileDTO> getUserProfile(String userId) {
        return HttpResult.success(basicUserService.getUserProfile(userId));
    }

    @PostMapping("/updateUserProfile")
    @ApiOperation("更新用户个人信息")
    public HttpResult updateUserProfile(@RequestBody UserProfileDTO profileDto) {
        if (basicUserService.updateUserProfile(profileDto)) {
            return HttpResult.success(UPDATE_SUCCESS_MSG);
        }
        return HttpResult.fail();
    }

}

