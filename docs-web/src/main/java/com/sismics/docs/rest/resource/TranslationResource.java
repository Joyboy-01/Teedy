package com.sismics.docs.rest.resource;

import java.io.StringReader;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sismics.docs.core.dao.FileDao;
import com.sismics.docs.core.model.jpa.File;
import com.sismics.docs.core.util.DirectoryUtil;
import com.sismics.docs.core.util.PDFTextExtractor;
import com.sismics.docs.core.util.TranslationUtil;
import com.sismics.rest.exception.ClientException;
import com.sismics.rest.exception.ServerException;
import com.sismics.util.LocaleUtil;

import java.nio.charset.StandardCharsets;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * 文档翻译REST资源。
 *
 * @author [您的姓名]
 */
@Path("/translation")
public class TranslationResource {
    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(TranslationResource.class);
    
    /**
     * 翻译文本。
     *
     * @param targetLanguage 目标语言代码
     * @param json 请求正文
     * @return 响应
     */
    @POST
    @Path("{targetLanguage: [a-z]{2}}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response translate(
            @PathParam("targetLanguage") String targetLanguage,
            String json) {
        // 解析请求体
        JsonObject jsonObject;
        try (JsonReader reader = Json.createReader(new StringReader(json))) {
            jsonObject = reader.readObject();
        } catch (Exception e) {
            throw new ClientException("InvalidRequest", "无效的请求格式");
        }
        
        if (!jsonObject.containsKey("text")) {
            throw new ClientException("ValidationError", "缺少text参数");
        }
        
        String text = jsonObject.getString("text");
        if (text.trim().isEmpty()) {
            throw new ClientException("ValidationError", "文本不能为空");
        }
        
        try {
            // 获取目标区域
            Locale targetLocale = LocaleUtil.getLocale(targetLanguage);
            
            // 调用翻译工具
            String translatedText = TranslationUtil.translateText(text, targetLocale);
            
            // 构建响应
            JsonObjectBuilder response = Json.createObjectBuilder()
                    .add("status", "ok")
                    .add("original", text)
                    .add("translated", translatedText)
                    .add("language", targetLanguage);
            
            return Response.ok().entity(response.build()).build();
        } catch (Exception e) {
            log.error("翻译失败", e);
            throw new ServerException("TranslationError", "翻译服务暂时不可用: " + e.getMessage());
        }
    }
    
    /**
     * 提取PDF文件文本。
     *
     * @param fileId 文件ID
     * @return 响应
     */
    @GET
    @Path("file/{fileId: [a-z0-9\\-]+}/text")
    @Produces(MediaType.APPLICATION_JSON)
    public Response extractFileText(@PathParam("fileId") String fileId) {
        try {
            // 获取文件信息
            FileDao fileDao = new FileDao();
            File file = fileDao.getActiveById(fileId);

            if (file == null) {
                throw new ClientException("NotFound", "文件不存在");
            }

            String extractedText;

            // 根据文件类型提取文本
            if ("application/pdf".equals(file.getMimeType())) {
                extractedText = PDFTextExtractor.extractText(fileId);
            } else if (file.getMimeType() != null && (
                       file.getMimeType().startsWith("text/") || 
                       file.getMimeType().indexOf("markdown") != -1)) {
                // 读取文本文件内容
                java.io.File storedFile = DirectoryUtil.getStorageDirectory().resolve(file.getId()).toFile();
                extractedText = FileUtils.readFileToString(storedFile, StandardCharsets.UTF_8);
            } else {
                throw new ClientException("UnsupportedFormat", "不支持提取此文件类型的文本");
            }

            // 构建响应
            JsonObjectBuilder response = Json.createObjectBuilder()
                    .add("status", "ok")
                    .add("text", extractedText);

            return Response.ok().entity(response.build()).build();
        } catch (Exception e) {
            log.error("文件文本提取失败", e);
            throw new ServerException("ExtractionError", "文件文本提取失败: " + e.getMessage());
        }
    }
}
