package com.aichat.features;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class WebSearchService {
    
    private static final String SEARCH_API = "https://api.duckduckgo.com/?q=%s&format=json&no_html=1&skip_disambig=1";
    private static final int MAX_RESULTS = 3;
    private static final int TIMEOUT_MS = 5000;
    
    public static CompletableFuture<String> search(String query) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
                String urlString = String.format(SEARCH_API, encodedQuery);
                
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(TIMEOUT_MS);
                conn.setReadTimeout(TIMEOUT_MS);
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                
                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    return "Search unavailable";
                }
                
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                
                return parseSearchResults(response.toString());
                
            } catch (Exception e) {
                System.err.println("[AI Chat] Web search error: " + e.getMessage());
                return "Search failed";
            }
        });
    }
    
    private static String parseSearchResults(String json) {
        try {
            JsonObject root = new JsonParser().parse(json).getAsJsonObject();
            
            StringBuilder results = new StringBuilder();
            results.append("[WEB SEARCH RESULTS]\n");
            
            if (root.has("AbstractText") && !root.get("AbstractText").getAsString().isEmpty()) {
                String abstractText = root.get("AbstractText").getAsString();
                results.append("Summary: ").append(abstractText).append("\n");
            }
            
            if (root.has("RelatedTopics") && root.get("RelatedTopics").isJsonArray()) {
                JsonArray topics = root.getAsJsonArray("RelatedTopics");
                int count = 0;
                
                for (int i = 0; i < topics.size() && count < MAX_RESULTS; i++) {
                    if (topics.get(i).isJsonObject()) {
                        JsonObject topic = topics.get(i).getAsJsonObject();
                        
                        if (topic.has("Text") && topic.has("FirstURL")) {
                            String text = topic.get("Text").getAsString();
                            String url = topic.get("FirstURL").getAsString();
                            
                            results.append(count + 1).append(". ").append(text).append("\n");
                            results.append("   Source: ").append(url).append("\n");
                            count++;
                        }
                    }
                }
            }
            
            if (results.toString().equals("[WEB SEARCH RESULTS]\n")) {
                return "No results found";
            }
            
            results.append("[END SEARCH]");
            return results.toString();
            
        } catch (Exception e) {
            System.err.println("[AI Chat] Parse error: " + e.getMessage());
            return "Failed to parse results";
        }
    }
    
    public static boolean shouldSearch(String message) {
        String lower = message.toLowerCase();
        
        return lower.contains("search") || lower.contains("look up") || 
               lower.contains("find info") || lower.contains("what is") ||
               lower.contains("who is") || lower.contains("when is") ||
               lower.contains("where is") || lower.contains("how to") ||
               lower.contains("google") || lower.contains("check online");
    }
    
    public static String extractQuery(String message) {
        String lower = message.toLowerCase();
        
        if (lower.contains("search for")) {
            return message.substring(message.toLowerCase().indexOf("search for") + 10).trim();
        }
        if (lower.contains("look up")) {
            return message.substring(message.toLowerCase().indexOf("look up") + 7).trim();
        }
        if (lower.contains("what is")) {
            return message.substring(message.toLowerCase().indexOf("what is") + 7).trim();
        }
        if (lower.contains("who is")) {
            return message.substring(message.toLowerCase().indexOf("who is") + 6).trim();
        }
        if (lower.contains("when is")) {
            return message.substring(message.toLowerCase().indexOf("when is") + 7).trim();
        }
        if (lower.contains("where is")) {
            return message.substring(message.toLowerCase().indexOf("where is") + 8).trim();
        }
        if (lower.contains("how to")) {
            return message.substring(message.toLowerCase().indexOf("how to") + 6).trim();
        }
        
        return message;
    }
}
