package com.zzl.umr.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 文件信息表(BasicFile)实体类
 *
 * @author zhangzl
 * @since 2026-01-16 11:32:16
 */
@Data
public class BasicFile implements Serializable {
    /**
     * id
     */
    private String id;

    /**
     * 存储引擎
     */
    private String engine;

    /**
     * 存储桶
     */
    private String bucket;

    /**
     * 文件名称
     */
    private String name;

    /**
     * 文件后缀
     */
    private String suffix;

    /**
     * 文件大小kb
     */
    private String sizeKb;

    /**
     * 文件大小（格式化后）
     */
    private String sizeInfo;

    /**
     * 文件的对象名（唯一名称）
     */
    private String objName;

    /**
     * 文件存储路径
     */
    private String storagePath;

    /**
     * 文件下载路径
     */
    private String downloadPath;

    /**
     * 图片缩略图
     */
    private String thumbnail;

    /**
     * 扩展信息
     */
    private String extJson;

    /**
     * 删除标志
     */
    private String deleteFlag;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

}
