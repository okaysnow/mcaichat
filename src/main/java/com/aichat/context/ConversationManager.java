package com.aichat.context;

import com.aichat.ai.ChatMessage;
import com.aichat.config.ModConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversationManager {
    
    private static final Map<String, ConversationContext> conversations = new HashMap<>();
    
    public static void addMessage(String player, String role, String content) {
        if (!ModConfig.rememberContext) {
            return;
        }
        
        ConversationContext context = conversations.get(player.toLowerCase());
        if (context == null) {
            context = new ConversationContext();
            conversations.put(player.toLowerCase(), context);
        }
        
        context.addMessage(new ChatMessage(role, content));
        context.updateLastInteraction();
    }
    
    public static List<ChatMessage> getContext(String player) {
        if (!ModConfig.rememberContext) {
            return new ArrayList<>();
        }
        
        ConversationContext context = conversations.get(player.toLowerCase());
        if (context == null || context.isExpired()) {
            return new ArrayList<>();
        }
        
        return context.getMessages();
    }
    
    public static void clearContext(String player) {
        conversations.remove(player.toLowerCase());
    }
    
    public static void clearAllContexts() {
        conversations.clear();
    }
    
    public static void cleanupExpired() {
        conversations.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    private static class ConversationContext {
        private final List<ChatMessage> messages;
        private long lastInteraction;
        
        public ConversationContext() {
            this.messages = new ArrayList<>();
            this.lastInteraction = System.currentTimeMillis();
        }
        
        public void addMessage(ChatMessage message) {
            messages.add(message);

            while (messages.size() > ModConfig.maxContextMessages) {
                messages.remove(0);
            }
        }
        
        public List<ChatMessage> getMessages() {
            return new ArrayList<>(messages);
        }
        
        public void updateLastInteraction() {
            this.lastInteraction = System.currentTimeMillis();
        }
        
        public boolean isExpired() {
            long timeoutMillis = ModConfig.contextTimeoutMinutes * 60 * 1000L;
            return (System.currentTimeMillis() - lastInteraction) > timeoutMillis;
        }
    }
}
