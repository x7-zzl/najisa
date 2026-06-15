package com.zzl.umr.controller;

import com.zzl.umr.model.SysMessage;
import com.zzl.umr.model.dto.HttpResult;
import com.zzl.umr.service.SysMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static com.zzl.umr.constants.MessageConstant.*;

/**
 * 系统消息表(SysMessage)表控制层
 *
 * @author zhangzl
 * @since 2026-01-30 14:42:17
 */
@RestController
@RequestMapping("sysMessage")
@Api(tags = "消息管理")
public class SysMessageController {
    /**
     * 服务对象
     */
    @Resource
    private SysMessageService sysMessageService;


    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/queryById")
    public HttpResult queryById(String id) {
        return HttpResult.success(sysMessageService.queryById(id));
    }


    /**
     * 新增数据
     *
     * @param sysMessage 实体
     * @return 新增结果
     */
    @PostMapping("/add")
    public HttpResult add(@RequestBody SysMessage sysMessage) {
        if (sysMessageService.add(sysMessage)) {
            return HttpResult.success(ADD_SUCCESS_MSG);
        }
        return HttpResult.fail();
    }

    /**
     * 编辑数据
     *
     * @param sysMessage 实体
     * @return 编辑结果
     */
    @PostMapping("/update")
    public HttpResult update(@RequestBody SysMessage sysMessage) {
        if (sysMessageService.updateById(sysMessage)) {
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
        if (sysMessageService.deleteById(id)) {
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
        if (sysMessageService.batchDelete(idList) > 0) {
            return HttpResult.success(DELETE_SUCCESS_MSG);
        }
        return HttpResult.fail();
    }

    @GetMapping("/unreadCount")
    @ApiOperation("获取未读消息数量")
    public HttpResult unreadCount() {
        String userName = getCurrentUserName();
        return HttpResult.success(sysMessageService.unreadCount(userName));
    }

    @GetMapping("/unread")
    @ApiOperation("全部消息列表（含已读状态）")
    public HttpResult unread() {
        String userName = getCurrentUserName();
        return HttpResult.success(sysMessageService.unread(userName));
    }

    @PostMapping("/markRead")
    @ApiOperation("单个消息已读")
    public HttpResult markRead(@RequestParam String messageId) {
        String userName = getCurrentUserName();
        if (sysMessageService.markRead(messageId, userName)) {
            return HttpResult.success(UPDATE_SUCCESS_MSG);
        }
        return HttpResult.fail();
    }

    @PostMapping("/markAllRead")
    @ApiOperation("全部消息已读")
    public HttpResult markAllRead() {
        String userName = getCurrentUserName();
        if (sysMessageService.markAllRead(userName)) {
            return HttpResult.success(UPDATE_SUCCESS_MSG);
        }
        return HttpResult.fail();
    }


    /**
     * 获取当前登录用户名
     */
    private String getCurrentUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

}
