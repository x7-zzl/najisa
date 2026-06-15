package com.zzl.umr.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.umr.config.exception.CommonServiceException;
import com.zzl.umr.enums.FileEngineTypeEnum;
import com.zzl.umr.mapper.BasicFileMapper;
import com.zzl.umr.model.BasicFile;
import com.zzl.umr.service.BasicFileService;
import com.zzl.umr.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangzl
 * @description 文件信息表(BasicFile)表服务实现类
 * @date 2026-01-16 11:17:39
 */
@Service("basicFileService")
@Slf4j
public class BasicFileServiceImpl extends ServiceImpl<BasicFileMapper, BasicFile> implements BasicFileService {
    @Resource
    private BasicFileMapper basicFileMapper;

    @Value("${file.storage.local-dir:./uploads}")
    private String localDir;

    @Value("${file.access.base-url:http://127.0.0.1:8088}")
    private String accessBaseUrl;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 通过ID查询单条数据
     *
     * @param basicFileId 主键
     * @return 实例对象
     */
    @Override
    public BasicFile queryById(String basicFileId) {
        return basicFileMapper.selectById(basicFileId);
    }

    /**
     * 查询数据列表
     *
     * @param basicFile 查询参数
     * @return 实例对象集合
     */
    @Override
    public List<BasicFile> queryList(BasicFile basicFile) {
        // 处理查询条件
        LambdaQueryWrapper<BasicFile> queryWrapper = new LambdaQueryWrapper<>();
        this.dealWrapper(queryWrapper, basicFile);

        List<BasicFile> basicFileList = basicFileMapper.selectList(queryWrapper);

        // 处理数据
        this.dealDataList(basicFileList);

        return basicFileList;
    }

    /**
     * 新增数据
     *
     * @param basicFile 实例对象
     * @return 是否成功
     */
    @Override
    public boolean add(BasicFile basicFile) {
        basicFile.setId(SnowflakeIdWorker.newId());
        log.info("插入一条数据:{}", basicFile);
        return basicFileMapper.insert(basicFile) > 0;
    }

    /**
     * 批量新增数据
     *
     * @param basicFileList 数据列表
     * @return 新增数量
     */
    @Override
    public Integer batchAdd(List<BasicFile> basicFileList) {
        if (CollectionUtil.isNotEmpty(basicFileList)) {
            log.info("批量插入{}条数据", basicFileList.size());

            for (BasicFile basicFile : basicFileList) {
                basicFile.setId(SnowflakeIdWorker.newId());
            }

            boolean saveBatch = this.saveBatch(basicFileList);
            return saveBatch ? basicFileList.size() : 0;
        }
        return 0;
    }

    /**
     * 修改数据
     *
     * @param basicFile 实例对象
     * @return 是否成功
     */
    @Override
    public boolean updateById(BasicFile basicFile) {
        log.info("修改一条数据:{}", basicFile);
        return basicFileMapper.updateById(basicFile) > 0;
    }

    /**
     * 通过主键删除数据
     *
     * @param basicFileId 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(String basicFileId) {
        log.info("删除一条数据，Id:{}", basicFileId);
        return basicFileMapper.deleteById(basicFileId) > 0;
    }

    /**
     * 批量删除数据
     *
     * @param idList 主键列表
     * @return 删除数量
     */
    @Override
    public Integer batchDelete(List<String> idList) {
        if (CollectionUtil.isNotEmpty(idList)) {
            log.info("批量删除{}条数据", idList.size());
            return basicFileMapper.deleteBatchIds(idList);
        }
        return 0;
    }

    @Override
    public String uploadReturnUrl(String engine, MultipartFile file) {
        return storageFile(engine, file, false);
    }

    @Override
    public String uploadReturnId(String engine, MultipartFile file) {
        return storageFile(engine, file, true);
    }

    @Override
    public boolean deleteLocalByUrl(String url) {
        if (StrUtil.isBlank(url)) {
            return false;
        }
        String fileKey = extractLocalFileKey(url);
        if (StrUtil.isBlank(fileKey)) {
            return false;
        }
        Path baseDir = Paths.get(localDir).toAbsolutePath().normalize();
        Path targetPath = baseDir.resolve(fileKey).normalize();
        if (!targetPath.startsWith(baseDir)) {
            return false;
        }
        try {
            return Files.deleteIfExists(targetPath);
        } catch (Exception e) {
            log.warn("删除本地文件失败: {}", targetPath, e);
            return false;
        }
    }

    /**
     * 处理查询条件
     *
     * @param queryWrapper 查询条件
     * @param basicFile    查询参数
     */
    private void dealWrapper(LambdaQueryWrapper<BasicFile> queryWrapper, BasicFile basicFile) {
    }

    /**
     * 处理数据列表
     *
     * @param basicFileList 数据列表
     */
    private void dealDataList(List<BasicFile> basicFileList) {
        if (CollectionUtil.isNotEmpty(basicFileList)) {
        }
    }


    /**
     * 返回文件id或者链接
     *
     * @param returnFileId true:返回文件id，false:返回文件链接
     * @param file         文件
     * @return 文件链接、文件id
     */
    private String storageFile(String engine, MultipartFile file, boolean returnFileId) {

        if (file == null || file.isEmpty()) {
            throw new CommonServiceException("上传文件不能为空");
        }

        // 如果引擎为空，默认使用本地
        if (ObjectUtil.isEmpty(engine)) {
            engine = FileEngineTypeEnum.LOCAL.getValue();
        }

        String fileId = SnowflakeIdWorker.newId();

        // 原始文件名（做个兜底与清理）
        String originalName = Objects.toString(file.getOriginalFilename(), "");
        originalName = originalName.replace("\\", "/");
        if (originalName.contains("/")) {
            originalName = StrUtil.subAfter(originalName, "/", true);
        }

        // 后缀
        String suffix = FileUtil.getSuffix(originalName);
        suffix = StrUtil.blankToDefault(suffix, null);

        // 对象名称：id.后缀
        String objName = (suffix != null) ? (fileId + StrUtil.DOT + suffix) : fileId;

        // 存储桶名称（本地固定）
        String bucketName;

        // 存储地址（本地：文件系统路径；其他：网络地址）
        String storageUrl;

        // 对外可访问的URL（本地：/files/...；其他：网络地址）
        String publicUrl;

        // 生成 key：yyyy/MM/dd/id.suffix
        String fileKey = genFileKey(fileId, file, suffix);

        if (engine.equals(FileEngineTypeEnum.LOCAL.getValue())) {

            bucketName = "defaultBucketName";

            // 真实落盘路径：{localDir}/{yyyy/MM/dd}/{id.suffix}
            Path baseDir = Paths.get(localDir).toAbsolutePath().normalize();
            Path targetPath = baseDir.resolve(fileKey).normalize();

            // 防止路径穿越：必须仍在 baseDir 下面
            if (!targetPath.startsWith(baseDir)) {
                throw new CommonServiceException("非法文件路径");
            }

            try {
                Files.createDirectories(targetPath.getParent());

                // 覆盖策略：同ID理论不会重复；用 REPLACE_EXISTING 更稳
                try (InputStream in = file.getInputStream()) {
                    Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (Exception e) {
                throw new CommonServiceException("本地文件保存失败：" + e.getMessage());
            }

            // 本地存储路径（落库用）
            storageUrl = targetPath.toString();

            // 对外可访问URL（关键：对应 WebConfig 的 /files/** 映射）
            publicUrl = buildPublicUrl(contextPath + "/files/" + fileKey);

        } else if (engine.equals(FileEngineTypeEnum.MINIO.getValue())) {

            // 先占位：后面接MinIO时再补
            bucketName = "yourMinioBucketName";
            storageUrl = "yourMinioObjectUrl";
            publicUrl = storageUrl;

        } else {
            throw new CommonServiceException("不支持的文件引擎：" + engine);
        }

        // 将文件信息保存到数据库
        BasicFile basicFile = new BasicFile();
        basicFile.setId(fileId);
        basicFile.setEngine(engine);
        basicFile.setBucket(bucketName);
        basicFile.setName(originalName);
        basicFile.setSuffix(suffix);

        basicFile.setSizeKb(Convert.toStr(NumberUtil.div(new BigDecimal(file.getSize()), BigDecimal.valueOf(1024)).setScale(0, BigDecimal.ROUND_HALF_UP)));
        basicFile.setSizeInfo(FileUtil.readableFileSize(file.getSize()));
        basicFile.setObjName(objName);
        basicFile.setStoragePath(storageUrl);

        // 如果是图片，则压缩生成缩略图
        if (ObjectUtil.isNotEmpty(suffix) && isPic(suffix)) {
            try {
                basicFile.setThumbnail(ImgUtil.toBase64DataUri(ImgUtil.scale(ImgUtil.toImage(file.getBytes()), 100, 100, null), suffix));
            } catch (Exception ignored) {
            }
        }

        // 下载地址：本地返回可直接访问的 publicUrl
        basicFile.setDownloadPath(publicUrl);

        // 入库
        save(basicFile);

        return returnFileId ? fileId : publicUrl;
    }

    /**
     * 拼接对外可访问URL
     */
    private String buildPublicUrl(String path) {
        String base = StrUtil.removeSuffix(accessBaseUrl, "/");
        String p = path.startsWith("/") ? path : ("/" + path);
        return base + p;
    }

    private String extractLocalFileKey(String url) {
        String u = url.trim();
        int q = u.indexOf('?');
        if (q >= 0) {
            u = u.substring(0, q);
        }
        int h = u.indexOf('#');
        if (h >= 0) {
            u = u.substring(0, h);
        }

        int idx = u.indexOf("/files/");
        if (idx < 0) {
            return null;
        }
        String key = u.substring(idx + "/files/".length());
        key = key.replace("\\", "/");
        while (key.startsWith("/")) {
            key = key.substring(1);
        }
        if (StrUtil.isBlank(key)) {
            return null;
        }
        if (key.contains("..")) {
            return null;
        }
        return key;
    }

    /**
     * 生成文件的key，格式如 2026/01/16/1377109572375810050.docx
     */
    public String genFileKey(String fileId, MultipartFile file, String suffix) {
        LocalDate now = LocalDate.now();
        String dateFolderPath = now.getYear() + "/" + String.format("%02d", now.getMonthValue()) + "/" + String.format("%02d", now.getDayOfMonth()) + "/";

        String objName = (ObjectUtil.isNotEmpty(suffix)) ? (fileId + StrUtil.DOT + suffix) : fileId;

        return dateFolderPath + objName;
    }

    /**
     * 根据文件后缀判断是否图片
     */
    private static boolean isPic(String fileSuffix) {
        fileSuffix = fileSuffix.toLowerCase();
        return ImgUtil.IMAGE_TYPE_GIF.equals(fileSuffix) || ImgUtil.IMAGE_TYPE_JPG.equals(fileSuffix) || ImgUtil.IMAGE_TYPE_JPEG.equals(fileSuffix) || ImgUtil.IMAGE_TYPE_BMP.equals(fileSuffix) || ImgUtil.IMAGE_TYPE_PNG.equals(fileSuffix) || ImgUtil.IMAGE_TYPE_PSD.equals(fileSuffix);
    }


}
