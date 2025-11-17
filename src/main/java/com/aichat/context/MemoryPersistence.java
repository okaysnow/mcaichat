package com.aichat.context;

import com.aichat.ai.ChatMessage;
import com.google.gson.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryPersistence {
    
    private static final File MEMORY_FILE = new File("config/memory.json");
    private static final Map<String, List<ChatMessage>> persistentMemory = new ConcurrentHashMap<>();
    private static final int MAX_MESSAGES_PER_PLAYER = 100;
    
    public static void saveMemory() {
        try {
            MEMORY_FILE.getParentFile().mkdirs();
            
            JsonObject root = new JsonObject();
            JsonObject players = new JsonObject();
            
            for (Map.Entry<String, List<ChatMessage>> entry : persistentMemory.entrySet()) {
                JsonArray messages = new JsonArray();
                for (ChatMessage msg : entry.getValue()) {
                    JsonObject msgObj = new JsonObject();
                    msgObj.addProperty("role", msg.getRole());
                    msgObj.addProperty("content", msg.getContent());
                    messages.add(msgObj);
                }
                players.add(entry.getKey(), messages);
            }
            
            root.add("players", players);
            root.addProperty("lastSaved", System.currentTimeMillis());
            
            FileWriter writer = new FileWriter(MEMORY_FILE);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(root, writer);
            writer.close();
            
            System.out.println("[AI Chat] Memory saved to " + MEMORY_FILE.getPath());
        } catch (Exception e) {
            System.err.println("[AI Chat] Failed to save memory: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void loadMemory() {
        if (!MEMORY_FILE.exists()) {
            System.out.println("[AI Chat] No memory file found, starting fresh");
            return;
        }
        
        try {
            FileReader reader = new FileReader(MEMORY_FILE);
            JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
            reader.close();
            
            if (root.has("players")) {
                JsonObject players = root.getAsJsonObject("players");
                for (Map.Entry<String, JsonElement> entry : players.entrySet()) {
                    List<ChatMessage> messages = new ArrayList<>();
                    JsonArray msgArray = entry.getValue().getAsJsonArray();
                    
                    for (JsonElement msgElem : msgArray) {
                        JsonObject msgObj = msgElem.getAsJsonObject();
                        String role = msgObj.get("role").getAsString();
                        String content = msgObj.get("content").getAsString();
                        
                        messages.add(new ChatMessage(role, content));
                    }
                    
                    persistentMemory.put(entry.getKey(), messages);
                }
            }
            
            System.out.println("[AI Chat] Memory loaded from " + MEMORY_FILE.getPath());
            System.out.println("[AI Chat] Loaded conversations with " + persistentMemory.size() + " players");
        } catch (Exception e) {
            System.err.println("[AI Chat] Failed to load memory: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void addToMemory(String playerName, String role, String content) {
        persistentMemory.putIfAbsent(playerName.toLowerCase(), new ArrayList<>());
        List<ChatMessage> messages = persistentMemory.get(playerName.toLowerCase());
        
        messages.add(new ChatMessage(role, content));
        
        while (messages.size() > MAX_MESSAGES_PER_PLAYER) {
            messages.remove(0);
        }
    }
    
    public static List<ChatMessage> getMemory(String playerName) {
        return persistentMemory.getOrDefault(playerName.toLowerCase(), new ArrayList<>());
    }
    
    public static void clearMemory(String playerName) {
        persistentMemory.remove(playerName.toLowerCase());
        saveMemory();
    }
    
    public static void clearAllMemory() {
        persistentMemory.clear();
        saveMemory();
    }
    
    public static Map<String, Integer> getMemoryStats() {
        Map<String, Integer> stats = new HashMap<>();
        for (Map.Entry<String, List<ChatMessage>> entry : persistentMemory.entrySet()) {
            stats.put(entry.getKey(), entry.getValue().size());
        }
        return stats;
    }
}
