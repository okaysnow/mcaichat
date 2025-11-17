package com.aichat.translation;

import com.aichat.config.ModConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TranslationService {
    
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    
    /**
     * Detect the language of the given text
     * @param text Text to analyze
     * @return CompletableFuture with ISO language code (e.g., "en", "es", "fr")
     */
    public static CompletableFuture<String> detectLanguage(String text) {
        return CompletableFuture.supplyAsync(() -> {
            if (!ModConfig.detectLanguage) {
                return "en";
            }
            
            try {

                if (text.matches(".*[\\u4e00-\\u9fa5]+.*")) return "zh";
                if (text.matches(".*[\\u3040-\\u309f\\u30a0-\\u30ff]+.*")) return "ja";
                if (text.matches(".*[\\u0400-\\u04ff]+.*")) return "ru";
                if (text.matches(".*[\\u0590-\\u05ff]+.*")) return "he";
                if (text.matches(".*[\\u0600-\\u06ff]+.*")) return "ar";

                return "en";
            } catch (Exception e) {
                e.printStackTrace();
                return "en";
            }
        }, executor);
    }
    
    /**
     * Translate text to the target language
     * @param text Text to translate
     * @param targetLang Target language code
     * @return CompletableFuture with translated text
     */
    public static CompletableFuture<String> translate(String text, String targetLang) {
        return CompletableFuture.supplyAsync(() -> {
            if (!ModConfig.autoTranslate) {
                return text;
            }
            
            try {

                String apiUrl = "https://libretranslate.com/translate";
                
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(10000);
                
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("q", text);
                requestBody.addProperty("source", "auto");
                requestBody.addProperty("target", targetLang);
                requestBody.addProperty("format", "text");
                
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(requestBody.toString().getBytes("utf-8"));
                }
                
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    br.close();
                    
                    JsonObject jsonResponse = new JsonParser().parse(response.toString()).getAsJsonObject();
                    return jsonResponse.get("translatedText").getAsString();
                } else {
                    System.err.println("Translation API error: " + responseCode);
                    return text;
                }
            } catch (Exception e) {
                System.err.println("Translation error: " + e.getMessage());
                return text;
            }
        }, executor);
    }
    
    /**
     * Translate text and detect source language automatically
     * @param text Text to translate
     * @return CompletableFuture with translated text
     */
    public static CompletableFuture<String> autoTranslate(String text) {
        return translate(text, ModConfig.targetLanguage);
    }
}
