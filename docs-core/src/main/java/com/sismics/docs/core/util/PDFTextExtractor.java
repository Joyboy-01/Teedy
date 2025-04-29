package com.sismics.docs.core.util;

import com.sismics.docs.core.dao.FileDao;
import com.sismics.docs.core.model.jpa.File;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * PDF文本提取工具类。
 * 
 * @author [您的姓名]
 */
public class PDFTextExtractor {
    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(PDFTextExtractor.class);
    
    /**
     * 提取PDF文件文本。
     * 
     * @param fileId 文件ID
     * @return 提取的文本
     */
    public static String extractText(String fileId) throws Exception {
        // 获取文件信息
        FileDao fileDao = new FileDao();
        File file = fileDao.getActiveById(fileId);
        if (file == null) {
            throw new Exception("文件不存在");
        }
        
        // 确保是PDF文件
        if (!"application/pdf".equals(file.getMimeType())) {
            throw new Exception("文件不是PDF格式");
        }
        
        // 获取文件存储路径
        Path storedFile = DirectoryUtil.getStorageDirectory().resolve(file.getId());
        if (!Files.exists(storedFile)) {
            throw new Exception("文件不存在于存储中");
        }
        
        // 使用现有依赖来提取PDF文本
        // 这里取决于项目中已有的PDF处理库，可能是PDFBox、iText或其他
        // 以下是使用PDFBox的示例，如果项目中没有PDFBox，可能需要调整为适合的库
        try (InputStream is = Files.newInputStream(storedFile)) {
            try (PDDocument document = PDDocument.load(is)) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);
                return text;
            } catch (Exception e) {
                log.error("PDF文档加载失败", e);
                throw new Exception("无法加载PDF文档: " + e.getMessage());
            }
        }
    }
}