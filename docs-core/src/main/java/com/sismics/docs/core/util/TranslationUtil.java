package com.sismics.docs.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * 文档翻译工具类。
 *
 * @author [您的姓名]
 */
public class TranslationUtil {
    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(TranslationUtil.class);
    
    /**
     * 翻译文本到目标语言。
     * 
     * @param text 要翻译的文本
     * @param targetLocale 目标区域
     * @return 翻译后的文本
     */
    public static String translateText(String text, Locale targetLocale) throws Exception {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        String targetLanguage = targetLocale.getLanguage();
        
        // 使用LibreTranslate API
        String apiUrl = "https://libretranslate.de/translate";
        String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8.toString());
        String postData = "q=" + encodedText + "&source=auto&target=" + targetLanguage + "&format=text";
        
        log.debug("调用翻译API: {}", apiUrl);
        
        @SuppressWarnings("deprecation")
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoOutput(true);
        
        try (OutputStream os = conn.getOutputStream()) {
            os.write(postData.getBytes(StandardCharsets.UTF_8));
        }
        
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("翻译服务返回错误代码: " + responseCode);
        }
        
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }
        
        // 解析JSON响应
        try (JsonReader jsonReader = Json.createReader(new StringReader(response.toString()))) {
            JsonObject jsonResponse = jsonReader.readObject();
            return jsonResponse.getString("translatedText");
        }
    }
}