package com.zzl.umr.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.umr.model.BasicFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author zhangzl
 * @description 文件信息表(BasicFile)表服务接口
 * @date 2026-01-16 11:17:40
 */
public interface BasicFileService extends IService<BasicFile> {

    /**
     * 通过ID查询单条数据
     *
     * @param basicFileId 主键
     * @return 实例对象
     */
    BasicFile queryById(String basicFileId);

    /**
     * 查询数据列表
     *
     * @param basicFile 查询参数
     * @return 实例对象集合
     */
    List<BasicFile> queryList(BasicFile basicFile);

    /**
     * 新增数据
     *
     * @param basicFile 实例对象
     * @return 是否成功
     */
    boolean add(BasicFile basicFile);

    /**
     * 批量新增数据
     *
     * @param basicFileList 数据列表
     * @return 新增数量
     */
    Integer batchAdd(List<BasicFile> basicFileList);

    /**
     * 修改数据
     *
     * @param basicFile 实例对象
     * @return 是否成功
     */
    boolean updateById(BasicFile basicFile);

    /**
     * 通过主键删除数据
     *
     * @param basicFileId 主键
     * @return 是否成功
     */
    boolean deleteById(String basicFileId);

    /**
     * 批量删除数据
     *
     * @param idList 主键列表
     * @return 删除数量
     */
    Integer batchDelete(List<String> idList);


    /**
     * MultipartFile文件上传，返回文件Url
     **/
    String uploadReturnUrl(String engine, MultipartFile file);

    /**
     * MultipartFile文件上传，返回文件id
     **/
    String uploadReturnId(String engine, MultipartFile file);

    boolean deleteLocalByUrl(String url);
}
