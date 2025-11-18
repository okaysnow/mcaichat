package com.aichat.ai;
import com.aichat.config.ModConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class OpenAIService implements AIService {
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    @Override
    public CompletableFuture<String> generateResponse(String message, List<ChatMessage> context, String personality, int maxLength, String username) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String apiKey = ModConfig.openaiApiKey;
                if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your-api-key-here")) {
                    return "AI not configured. Set API key in config.";
                }
                URL url = new URL(API_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + apiKey);
                conn.setDoOutput(true);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(15000);
                JsonObject requestBody = buildRequestBody(message, context, personality, maxLength, username);
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
                    String content = jsonResponse.getAsJsonArray("choices")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("message")
                        .get("content").getAsString();
                    return content.trim();
                } else {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
                    StringBuilder error = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        error.append(line);
                    }
                    br.close();
                    System.err.println("OpenAI API Error: " + error.toString());
                    return "Error: API request failed.";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }, executor);
    }
    private JsonObject buildRequestBody(String message, List<ChatMessage> context, String personality, int maxLength, String username) {
        JsonObject body = new JsonObject();
        body.addProperty("model", ModConfig.openaiModel);
        body.addProperty("max_tokens", maxLength * 2);
        body.addProperty("temperature", 0.7);
        JsonArray messages = new JsonArray();
        JsonObject systemMsg = new JsonObject();
        systemMsg.addProperty("role", "system");
        systemMsg.addProperty("content", getSystemPrompt(personality, maxLength, username));
        messages.add(systemMsg);
        if (context != null && !context.isEmpty()) {
            for (ChatMessage msg : context) {
                JsonObject contextMsg = new JsonObject();
                contextMsg.addProperty("role", msg.getRole());
                contextMsg.addProperty("content", msg.getContent());
                messages.add(contextMsg);
            }
        }
        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", message);
        messages.add(userMsg);
        body.add("messages", messages);
        return body;
    }
    private String getSystemPrompt(String personality, int maxLength, String username) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("CRITICAL IDENTITY RULE: You are ").append(username).append(", a real Minecraft player. NEVER mention AI, bot, assistant, or automated responses. NEVER say 'as an AI' or similar phrases. You are a human player named ").append(username).append(" typing responses yourself. ");
        prompt.append("You are a helpful Minecraft player on Hypixel server. ");
        prompt.append("Keep responses very concise and under ").append(maxLength).append(" words. ");
        prompt.append("Use gaming/Minecraft slang when appropriate. ");
        switch (personality.toLowerCase()) {
            case "friendly":
                prompt.append("Be warm, encouraging, and supportive.");
                break;
            case "sarcastic":
                prompt.append("Use light sarcasm and wit, but stay friendly.");
                break;
            case "professional":
                prompt.append("Be informative and direct.");
                break;
            case "funny":
                prompt.append("Be humorous and entertaining, use jokes when fitting.");
                break;
            default:
                prompt.append("Be helpful and conversational.");
                break;
        }
        return prompt.toString();
    }
    @Override
    public boolean isConfigured() {
        String key = ModConfig.openaiApiKey;
        return key != null && !key.isEmpty() && !key.equals("your-api-key-here");
    }
    @Override
    public String getServiceName() {
        return "OpenAI";
    }
}
