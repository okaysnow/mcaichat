package com.aichat.features;

import com.aichat.ai.ChatMessage;
import java.util.*;
import java.util.stream.Collectors;

public class SmartMemory {
    
    private static final long ONE_HOUR = 3600000;
    private static final long ONE_DAY = 86400000;
    private static final long ONE_WEEK = 604800000;
    
    public static List<ChatMessage> prioritizeMemories(List<ChatMessage> messages, int maxMessages) {
        if (messages.size() <= maxMessages) {
            return messages;
        }
        
        List<ScoredMessage> scored = new ArrayList<>();
        long now = System.currentTimeMillis();
        
        for (int i = 0; i < messages.size(); i++) {
            ChatMessage msg = messages.get(i);
            double score = calculateImportanceScore(msg, i, messages.size(), now);
            scored.add(new ScoredMessage(msg, score));
        }
        
        scored.sort((a, b) -> Double.compare(b.score, a.score));
        
        return scored.stream()
            .limit(maxMessages)
            .sorted((a, b) -> Integer.compare(
                messages.indexOf(a.message), 
                messages.indexOf(b.message)
            ))
            .map(sm -> sm.message)
            .collect(Collectors.toList());
    }
    
    private static double calculateImportanceScore(ChatMessage msg, int position, int totalMessages, long now) {
        double score = 0;
        
        // Recency score (newer = higher)
        double recency = (double) position / totalMessages;
        score += recency * 40;
        
        // Keep the last 3 messages always
        if (position >= totalMessages - 3) {
            score += 30;
        }
        
        String content = msg.getContent().toLowerCase();
        
        // Important keywords
        if (containsImportantKeyword(content)) {
            score += 20;
        }
        
        // Questions are important
        if (content.contains("?") || content.startsWith("how") || content.startsWith("what") || 
            content.startsWith("why") || content.startsWith("where") || content.startsWith("when")) {
            score += 15;
        }
        
        // Game strategies and tips
        if (content.contains("strategy") || content.contains("tip") || content.contains("trick") ||
            content.contains("help") || content.contains("advice")) {
            score += 15;
        }
        
        // Personal information
        if (content.contains("i like") || content.contains("i love") || content.contains("i hate") ||
            content.contains("i prefer") || content.contains("my favorite")) {
            score += 20;
        }
        
        // Agreements and confirmations
        if (content.matches("^(ok|okay|yes|yeah|sure|thanks|thank you)$")) {
            score -= 10; // These are less important
        }
        
        // Length matters (longer messages often more meaningful)
        int wordCount = content.split("\\s+").length;
        if (wordCount > 10) {
            score += 10;
        } else if (wordCount < 3) {
            score -= 5;
        }
        
        return score;
    }
    
    private static boolean containsImportantKeyword(String content) {
        String[] importantWords = {
            "remember", "important", "always", "never", "must", "should",
            "dont", "do not", "please", "help", "learn", "teach",
            "skill", "win", "lose", "better", "improve", "practice"
        };
        
        for (String word : importantWords) {
            if (content.contains(word)) {
                return true;
            }
        }
        return false;
    }
    
    public static List<ChatMessage> decayOldMemories(List<ChatMessage> messages) {
        long now = System.currentTimeMillis();
        
        return messages.stream()
            .filter(msg -> {
                long age = now - msg.getTimestamp();
                
                // Always keep messages from last hour
                if (age < ONE_HOUR) {
                    return true;
                }
                
                // Keep 80% of messages from last day
                if (age < ONE_DAY) {
                    return Math.random() < 0.8;
                }
                
                // Keep 40% of messages from last week
                if (age < ONE_WEEK) {
                    return Math.random() < 0.4;
                }
                
                // Keep only 10% of older messages
                return Math.random() < 0.1;
            })
            .collect(Collectors.toList());
    }
    
    public static List<ChatMessage> filterMeaningfulContext(List<ChatMessage> messages, int targetSize) {
        // First, decay old memories
        List<ChatMessage> decayed = decayOldMemories(messages);
        
        // Then prioritize remaining messages
        return prioritizeMemories(decayed, targetSize);
    }
    
    public static boolean isImportantMessage(String content) {
        content = content.toLowerCase();
        
        // Very short messages are usually not important
        if (content.split("\\s+").length < 3) {
            return false;
        }
        
        // Questions are important
        if (content.contains("?")) {
            return true;
        }
        
        // Personal preferences
        if (content.contains("i like") || content.contains("i love") || 
            content.contains("my favorite") || content.contains("i prefer")) {
            return true;
        }
        
        // Important instructions
        if (content.contains("remember") || content.contains("important") || 
            content.contains("always") || content.contains("never")) {
            return true;
        }
        
        // Game strategies
        if (content.contains("strategy") || content.contains("tactic") || 
            content.contains("tip") || content.contains("trick")) {
            return true;
        }
        
        return false;
    }
    
    private static class ScoredMessage {
        ChatMessage message;
        double score;
        
        ScoredMessage(ChatMessage message, double score) {
            this.message = message;
            this.score = score;
        }
    }
}
