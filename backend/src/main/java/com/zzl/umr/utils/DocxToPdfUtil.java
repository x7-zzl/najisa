package com.zzl.umr.utils;

/**
 * @author zhangzl
 * @description docx转pdf工具类
 * @date 2026/02/09 16:44:33
 */

import org.docx4j.Docx4J;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.File;
import java.io.FileOutputStream;

public class DocxToPdfUtil {

    /**
     * 将 docx 文件转换为 PDF
     *
     * @param docxPath 输入文件路径
     * @param pdfPath  输出文件路径
     */
    public static void convert(String docxPath, String pdfPath) {
        try {
            // 1. 加载 Word 文档
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new File(docxPath));

            // 2. 配置字体映射（防止中文乱码）
            Mapper fontMapper = new IdentityPlusMapper();
            PhysicalFonts.discoverPhysicalFonts();

            PhysicalFont simsun = PhysicalFonts.get("SimSun");
            if (simsun != null) {
                fontMapper.put("SimSun", simsun);
                // 常用中文字体映射表
                fontMapper.put("隶书", PhysicalFonts.get("LiSu"));
                fontMapper.put("宋体", PhysicalFonts.get("SimSun"));
                fontMapper.put("微软雅黑", PhysicalFonts.get("Microsoft YaHei"));
                fontMapper.put("黑体", PhysicalFonts.get("SimHei"));
                fontMapper.put("楷体", PhysicalFonts.get("KaiTi"));
                fontMapper.put("新宋体", PhysicalFonts.get("NSimSun"));
                fontMapper.put("华文行楷", PhysicalFonts.get("STXingkai"));
                fontMapper.put("华文仿宋", PhysicalFonts.get("STFangsong"));
                fontMapper.put("仿宋", PhysicalFonts.get("FangSong"));
                fontMapper.put("幼圆", PhysicalFonts.get("YouYuan"));
                fontMapper.put("华文宋体", PhysicalFonts.get("STSong"));
                fontMapper.put("华文中宋", PhysicalFonts.get("STZhongsong"));
                fontMapper.put("等线", PhysicalFonts.get("SimSun"));
                fontMapper.put("等线 Light", PhysicalFonts.get("SimSun"));
                fontMapper.put("华文琥珀", PhysicalFonts.get("STHupo"));
                fontMapper.put("华文隶书", PhysicalFonts.get("STLiti"));
                fontMapper.put("华文新魏", PhysicalFonts.get("STXinwei"));
                fontMapper.put("华文彩云", PhysicalFonts.get("STCaiyun"));
                fontMapper.put("方正姚体", PhysicalFonts.get("FZYaoti"));
                fontMapper.put("方正舒体", PhysicalFonts.get("FZShuTi"));
                fontMapper.put("华文细黑", PhysicalFonts.get("STXihei"));
                fontMapper.put("宋体扩展", PhysicalFonts.get("simsun-extB"));
                fontMapper.put("仿宋_GB2312", PhysicalFonts.get("FangSong_GB2312"));
                fontMapper.put("新細明體", PhysicalFonts.get("SimSun"));

                // ⚙️ 修复 “宋体（正文）/宋体（标题）” 乱码
                PhysicalFonts.put("PMingLiU", PhysicalFonts.get("SimSun"));
                PhysicalFonts.put("新細明體", PhysicalFonts.get("SimSun"));
                wordMLPackage.setFontMapper(fontMapper);
            }

            // 3. 创建输出流并执行转换
            try (FileOutputStream os = new FileOutputStream(pdfPath)) {
                Docx4J.toPDF(wordMLPackage, os);
            }

            System.out.println("✅ PDF 生成成功：" + pdfPath);
        } catch (Exception e) {
            System.err.println("❌ 转换失败：" + e.getMessage());
        }
    }
}