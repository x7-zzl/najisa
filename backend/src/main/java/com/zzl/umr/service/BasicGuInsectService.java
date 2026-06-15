package com.zzl.umr.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.umr.model.BasicGuInsect;
import com.zzl.umr.model.cdn.BaseQueryCdn;

import java.util.List;

/**
 * @author zhangzl
 * @description 蛊虫信息表(BasicGuInsect)表服务接口
 * @date 2026-01-22 17:04:03
 */
public interface BasicGuInsectService extends IService<BasicGuInsect> {

    /**
     * 通过ID查询单条数据
     *
     * @param basicGuInsectId 主键
     * @return 实例对象
     */
    BasicGuInsect queryById(String basicGuInsectId);

    /**
     * 查询数据列表
     *
     * @param basicGuInsect 查询参数
     * @return 实例对象集合
     */
    List<BasicGuInsect> queryList(BasicGuInsect basicGuInsect);

    /**
     * 新增数据
     *
     * @param basicGuInsect 实例对象
     * @return 是否成功
     */
    boolean add(BasicGuInsect basicGuInsect);

    /**
     * 批量新增数据
     *
     * @param basicGuInsectList 数据列表
     * @return 新增数量
     */
    Integer batchAdd(List<BasicGuInsect> basicGuInsectList);

    /**
     * 修改数据
     *
     * @param basicGuInsect 实例对象
     * @return 是否成功
     */
    boolean updateById(BasicGuInsect basicGuInsect);

    /**
     * 通过主键删除数据
     *
     * @param basicGuInsectId 主键
     * @return 是否成功
     */
    boolean deleteById(String basicGuInsectId);

    /**
     * 批量删除数据
     *
     * @param idList 主键列表
     * @return 删除数量
     */
    Integer batchDelete(List<String> idList);

    /**
     * 分页查询数据列表
     *
     * @param queryCdn 查询参数
     * @return 列表
     */
    Page<BasicGuInsect> queryListPage(BaseQueryCdn queryCdn);
}


