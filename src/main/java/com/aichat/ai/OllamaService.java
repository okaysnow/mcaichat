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

public class OllamaService implements AIService {
    
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private String apiUrl = "http://localhost:11434/api/generate";
    
    @Override
    public CompletableFuture<String> generateResponse(String message, List<ChatMessage> context, String personality, int maxLength) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(30000);
                
                JsonObject requestBody = buildRequestBody(message, context, personality, maxLength);
                
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(requestBody.toString().getBytes("utf-8"));
                }
                
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = br.readLine()) != null) {
                        try {
                            JsonObject jsonLine = new JsonParser().parse(line).getAsJsonObject();
                            if (jsonLine.has("response")) {
                                response.append(jsonLine.get("response").getAsString());
                            }
                            if (jsonLine.has("done") && jsonLine.get("done").getAsBoolean()) {
                                break;
                            }
                        } catch (Exception e) {

                        }
                    }
                    br.close();
                    
                    return response.toString().trim();
                } else {
                    System.err.println("Ollama API Error: " + responseCode);
                    return "Error: Ollama not available. Make sure Ollama is running.";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }, executor);
    }
    
    private JsonObject buildRequestBody(String message, List<ChatMessage> context, String personality, int maxLength) {
        JsonObject body = new JsonObject();
        body.addProperty("model", "llama2");
        body.addProperty("stream", true);
        
        StringBuilder prompt = new StringBuilder();
        prompt.append(getSystemPrompt(personality, maxLength)).append("\n\n");

        if (context != null && !context.isEmpty()) {
            for (ChatMessage msg : context) {
                prompt.append(msg.getRole().equals("user") ? "User: " : "Assistant: ");
                prompt.append(msg.getContent()).append("\n");
            }
        }
        
        prompt.append("User: ").append(message).append("\nAssistant:");
        
        body.addProperty("prompt", prompt.toString());
        return body;
    }
    
    private String getSystemPrompt(String personality, int maxLength) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a helpful Minecraft player assistant on Hypixel server. ");
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

        return true;
    }
    
    @Override
    public String getServiceName() {
        return "Ollama (Local)";
    }
}
