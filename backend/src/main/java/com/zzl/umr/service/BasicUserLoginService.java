package com.zzl.umr.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.umr.model.BasicUserLogin;

import java.util.List;

/**
 * @author zhangzl
 * @description (BasicUserLogin)表服务接口
 * @date 2025-10-20 22:09:48
 */
public interface BasicUserLoginService extends IService<BasicUserLogin> {

    /**
     * 通过ID查询单条数据
     *
     * @param basicUserLoginId 主键
     * @return 实例对象
     */
    BasicUserLogin queryById(String basicUserLoginId);

    /**
     * 查询数据列表
     *
     * @param basicUserLogin 查询参数
     * @return 实例对象集合
     */
    List<BasicUserLogin> queryList(BasicUserLogin basicUserLogin);

    /**
     * 新增数据
     *
     * @param basicUserLogin 实例对象
     * @return 是否成功
     */
    boolean add(BasicUserLogin basicUserLogin);

    /**
     * 批量新增数据
     *
     * @param basicUserLoginList 数据列表
     * @return 新增数量
     */
    Integer batchAdd(List<BasicUserLogin> basicUserLoginList);

    /**
     * 修改数据
     *
     * @param basicUserLogin 实例对象
     * @return 是否成功
     */
    boolean updateById(BasicUserLogin basicUserLogin);

    /**
     * 通过主键删除数据
     *
     * @param basicUserLoginId 主键
     * @return 是否成功
     */
    boolean deleteById(String basicUserLoginId);

    /**
     * 批量删除数据
     *
     * @param idList 主键列表
     * @return 删除数量
     */
    Integer batchDelete(List<String> idList);

    /**
     * 异步更新登录信息
     * @param userName 用户名
     */
    void asyncUpdateLoginInfo(String userName);
}
