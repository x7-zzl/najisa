package com.zzl.umr.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zzl.umr.enums.FileEngineTypeEnum;
import com.zzl.umr.model.BasicFile;
import com.zzl.umr.model.dto.HttpResult;
import com.zzl.umr.service.BasicFileService;
import com.zzl.umr.utils.DocxToPdfUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.List;

import static com.zzl.umr.constants.MessageConstant.ADD_SUCCESS_MSG;
import static com.zzl.umr.constants.MessageConstant.DELETE_SUCCESS_MSG;
import static com.zzl.umr.constants.MessageConstant.UPDATE_SUCCESS_MSG;

/**
 * 文件信息表(BasicFile)表控制层
 *
 * @author zhangzl
 * @since 2026-01-16 11:17:40
 */
@RestController
@RequestMapping("basicFile")
@Api(tags = "文件信息管理")
public class BasicFileController {
    /**
     * 服务对象
     */
    @Resource
    private BasicFileService basicFileService;


    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/queryById")
    public HttpResult queryById(String id) {
        return HttpResult.success(basicFileService.queryById(id));
    }

    /**
     * 新增数据
     *
     * @param basicFile 实体
     * @return 新增结果
     */
    @PostMapping("/add")
    public HttpResult add(@RequestBody BasicFile basicFile) {
        if (basicFileService.add(basicFile)) {
            return HttpResult.success(ADD_SUCCESS_MSG);
        }
        return HttpResult.fail();
    }

    /**
     * 编辑数据
     *
     * @param basicFile 实体
     * @return 编辑结果
     */
    @PostMapping("/update")
    public HttpResult update(@RequestBody BasicFile basicFile) {
        if (basicFileService.updateById(basicFile)) {
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
        if (basicFileService.deleteById(id)) {
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
        if (basicFileService.batchDelete(idList) > 0) {
            return HttpResult.success(DELETE_SUCCESS_MSG);
        }
        return HttpResult.fail();
    }


    @ApiOperationSupport(order = 1)
    @ApiOperation("上传本地文件返回url")
    @PostMapping("/uploadLocalReturnUrl")
    public HttpResult<String> uploadLocalReturnUrl(@RequestPart("file") MultipartFile file) {
        return HttpResult.success(basicFileService.uploadReturnUrl(FileEngineTypeEnum.LOCAL.getValue(), file));
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("按URL删除本地文件")
    @PostMapping("/deleteLocalByUrl")
    public HttpResult<Boolean> deleteLocalByUrl(@RequestParam("url") String url) {
        return HttpResult.success(basicFileService.deleteLocalByUrl(url));
    }


    @GetMapping("/convertToPdf")
    public void convertToPdf(@RequestParam String filePath, HttpServletResponse response) throws Exception {
        // 检查文件是否存在
        File inputFile = new File(filePath);
        if (!inputFile.exists()) {
            throw new RuntimeException("文件不存在: " + filePath);
        }

        // 定义输出路径（临时文件）
        String pdfPath = filePath.replace(".docx", ".pdf");

        // 调用转换工具
        DocxToPdfUtil.convert(filePath, pdfPath);

        // 设置响应头并输出 PDF 文件
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" + new File(pdfPath).getName());
        try (FileInputStream fis = new FileInputStream(pdfPath);
             OutputStream os = response.getOutputStream()) {
            fis.transferTo(os);
            os.flush();
        }
        // 删除临时 PDF 文件
        new File(pdfPath).delete();
    }
    
}
