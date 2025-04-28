package com.sismics.docs.rest.resource;

import com.sismics.util.LocaleUtil; 
import com.sismics.docs.core.util.TranslationUtil;
import com.sismics.rest.exception.ClientException;
import com.sismics.rest.exception.ServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.StringReader;
import java.util.Locale;

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
}