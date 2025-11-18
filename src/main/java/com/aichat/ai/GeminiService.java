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
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
public class GeminiService implements AIService {
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent";
    @Override
    public CompletableFuture<String> generateResponse(String message, List<ChatMessage> context, String personality, int maxLength, String username) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String urlWithKey = API_URL + "?key=" + ModConfig.geminiApiKey;
                URL url = new URL(urlWithKey);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                String fullPrompt = buildPromptWithPersonality(message, personality, maxLength, username);
                if (context != null && !context.isEmpty()) {
                    StringBuilder contextBuilder = new StringBuilder();
                    contextBuilder.append("Previous conversation:\n");
                    for (ChatMessage msg : context) {
                        contextBuilder.append(msg.getRole()).append(": ").append(msg.getContent()).append("\n");
                    }
                    contextBuilder.append("\nCurrent message: ").append(fullPrompt);
                    fullPrompt = contextBuilder.toString();
                }
                JsonObject requestBody = new JsonObject();
                JsonArray contents = new JsonArray();
                JsonObject content = new JsonObject();
                JsonArray parts = new JsonArray();
                JsonObject part = new JsonObject();
                part.addProperty("text", fullPrompt);
                parts.add(part);
                content.add("parts", parts);
                contents.add(content);
                requestBody.add("contents", contents);
                JsonObject generationConfig = new JsonObject();
                generationConfig.addProperty("temperature", 0.9);
                generationConfig.addProperty("maxOutputTokens", maxLength * 2);
                requestBody.add("generationConfig", generationConfig);
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    JsonObject jsonResponse = new JsonParser().parse(response.toString()).getAsJsonObject();
                    if (jsonResponse.has("candidates")) {
                        JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
                        if (candidates.size() > 0) {
                            JsonObject candidate = candidates.get(0).getAsJsonObject();
                            JsonObject contentObj = candidate.getAsJsonObject("content");
                            JsonArray partsArray = contentObj.getAsJsonArray("parts");
                            if (partsArray.size() > 0) {
                                String text = partsArray.get(0).getAsJsonObject().get("text").getAsString();
                                return formatResponse(text, maxLength);
                            }
                        }
                    }
                    return "I couldn't generate a response.";
                } else {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
                    StringBuilder errorResponse = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        errorResponse.append(responseLine.trim());
                    }
                    System.err.println("[AI Chat] Gemini API Error: " + errorResponse.toString());
                    if (responseCode == 403) {
                        return "Gemini API key is invalid or not authorized.";
                    } else if (responseCode == 429) {
                        return "Rate limit reached. Please wait a moment.";
                    }
                    return "Error: Unable to connect to Gemini API (Code: " + responseCode + ")";
                }
            } catch (Exception e) {
                System.err.println("[AI Chat] Gemini error: " + e.getMessage());
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        });
    }
    @Override
    public boolean isConfigured() {
        return ModConfig.geminiApiKey != null && !ModConfig.geminiApiKey.equals("your-api-key-here");
    }
    @Override
    public String getServiceName() {
        return "Gemini";
    }
    private String buildPromptWithPersonality(String message, String personality, int maxLength, String username) {
        String identityInstruction = "CRITICAL IDENTITY RULE: You are " + username + ", a real Minecraft player. NEVER mention AI, bot, assistant, or automated responses. NEVER say 'as an AI' or similar phrases. You are a human player named " + username + " typing responses yourself. ";
        String personalityPrompt = "";
        switch (personality.toLowerCase()) {
            case "friendly":
                personalityPrompt = "You are a friendly and warm Minecraft player. Be welcoming and helpful. ";
                break;
            case "sarcastic":
                personalityPrompt = "You are a sarcastic Minecraft player. Use wit and irony in your responses. ";
                break;
            case "professional":
                personalityPrompt = "You are a professional and formal assistant. Be clear and business-like. ";
                break;
            case "funny":
                personalityPrompt = "You are a funny and humorous Minecraft player. Make jokes and keep things light. ";
                break;
            case "casual":
                personalityPrompt = "You are a casual and relaxed Minecraft player. Be chill and informal. ";
                break;
            case "mocking":
                personalityPrompt = "You are a mocking and teasing Minecraft player. Playfully make fun of messages in a lighthearted way. ";
                break;
        }
        return identityInstruction + personalityPrompt + "Respond to this Minecraft chat message in " + 
               maxLength + " words or less. Keep it brief and natural for Minecraft chat. Message: " + message;
    }
    private String formatResponse(String response, int maxLength) {
        response = response.trim();
        String[] words = response.split("\\s+");
        if (words.length > maxLength) {
            StringBuilder limited = new StringBuilder();
            for (int i = 0; i < maxLength; i++) {
                limited.append(words[i]).append(" ");
            }
            response = limited.toString().trim();
        }
        return response;
    }
}
