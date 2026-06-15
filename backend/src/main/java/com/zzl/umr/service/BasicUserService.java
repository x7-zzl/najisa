package com.zzl.umr.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.umr.model.BasicUser;
import com.zzl.umr.model.cdn.BaseQueryCdn;
import com.zzl.umr.model.cdn.LoginCdn;
import com.zzl.umr.model.dto.HttpResult;
import com.zzl.umr.model.dto.UserProfileDTO;

import java.util.List;


/**
 * @author zhangzl
 * @description 用户信息表(BasicUser)表服务接口
 * @date 2025/10/14 23:52
 */
public interface BasicUserService extends IService<BasicUser> {

    /**
     * 通过ID查询单条数据
     *
     * @param userId 主键
     * @return 实例对象
     */
    BasicUser queryById(String userId);

    /**
     * 查询数据列表
     *
     * @param basicUser 查询参数
     * @return 实例对象集合
     */
    List<BasicUser> queryList(BasicUser basicUser);

    /**
     * 新增数据
     *
     * @param basicUser 实例对象
     * @return 实例对象
     */
    boolean add(BasicUser basicUser);

    /**
     * 批量新增数据
     *
     * @param basicUserList
     * @return
     */
    Integer batchAdd(List<BasicUser> basicUserList);

    /**
     * 修改数据
     *
     * @param basicUser 实例对象
     * @return 实例对象
     */
    boolean updateById(BasicUser basicUser);

    /**
     * 通过主键删除数据
     *
     * @param userId 主键
     * @return 是否成功
     */
    boolean deleteById(String userId);


    /**
     * 批量删除数据
     *
     * @param idList
     * @return
     */
    Integer batchDelete(List<String> idList);

    /**
     * 登录
     *
     * @param logInCdn 登录参数
     * @return 登录结果
     */
    HttpResult login(LoginCdn logInCdn);

    /**
     * 退出登录
     * @return 退出结果
     */
    HttpResult logout();

    /**
     *  注册
     * @param logInCdn 注册参数
     * @return 注册结果
     */
    HttpResult<String> register(LoginCdn logInCdn);

    /**
     * 忘记密码
     * @param logInCdn 忘记密码参数
     * @return 忘记密码结果
     */
    HttpResult<String> forgetPassword(LoginCdn logInCdn);

    /**
     * 分页查询
     * @param queryCdn 查询参数
     * @return 分页结果
     */
    Page<BasicUser> queryListPage(BaseQueryCdn queryCdn);

    /**
     * 获取用户个人信息
     * @param userId 用户ID
     * @return 个人信息
     */
    UserProfileDTO getUserProfile(String userId);

    /**
     * 更新用户个人信息
     * @param profileDto 个人信息 DTO
     * @return 是否成功
     */
    boolean updateUserProfile(UserProfileDTO profileDto);
}
