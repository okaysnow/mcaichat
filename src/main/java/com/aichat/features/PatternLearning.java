package com.aichat.features;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PatternLearning {
    private static final Map<String, PlayerPatterns> playerPatterns = new ConcurrentHashMap<>();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static File patternsFile;
    
    public static void load(File configDir) {
        patternsFile = new File(configDir, "patterns.json");
        if (!patternsFile.exists()) {
            return;
        }
        
        try (FileReader reader = new FileReader(patternsFile)) {
            Type type = new TypeToken<Map<String, PlayerPatterns>>(){}.getType();
            Map<String, PlayerPatterns> loaded = gson.fromJson(reader, type);
            if (loaded != null) {
                playerPatterns.putAll(loaded);
            }
            System.out.println("[AI Chat] Loaded patterns for " + playerPatterns.size() + " players");
        } catch (Exception e) {
            System.err.println("[AI Chat] Error loading patterns: " + e.getMessage());
        }
    }
    
    public static void save() {
        try {
            patternsFile.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(patternsFile)) {
                gson.toJson(playerPatterns, writer);
            }
        } catch (Exception e) {
            System.err.println("[AI Chat] Error saving patterns: " + e.getMessage());
        }
    }
    
    public static void trackQuestion(String player, String question) {
        PlayerPatterns patterns = playerPatterns.computeIfAbsent(player, k -> new PlayerPatterns());
        
        String questionType = categorizeQuestion(question);
        patterns.questionTypes.put(questionType, patterns.questionTypes.getOrDefault(questionType, 0) + 1);
        
        patterns.totalInteractions++;
        patterns.lastInteraction = System.currentTimeMillis();
        save();
    }
    
    public static void trackResponse(String player, String question, String response, boolean successful) {
        PlayerPatterns patterns = playerPatterns.computeIfAbsent(player, k -> new PlayerPatterns());
        
        if (successful) {
            patterns.successfulPatterns.add(new ResponsePattern(question, response, System.currentTimeMillis()));
            if (patterns.successfulPatterns.size() > 50) {
                patterns.successfulPatterns.remove(0);
            }
        }
        
        int wordCount = response.split("\\s+").length;
        patterns.preferredLength.add(wordCount);
        if (patterns.preferredLength.size() > 20) {
            patterns.preferredLength.remove(0);
        }
        
        save();
    }
    
    public static void trackConversationLength(String player, int messageCount) {
        PlayerPatterns patterns = playerPatterns.computeIfAbsent(player, k -> new PlayerPatterns());
        patterns.conversationLengths.add(messageCount);
        if (patterns.conversationLengths.size() > 20) {
            patterns.conversationLengths.remove(0);
        }
        save();
    }
    
    public static void trackTopic(String player, String topic) {
        PlayerPatterns patterns = playerPatterns.computeIfAbsent(player, k -> new PlayerPatterns());
        patterns.topicInterests.put(topic, patterns.topicInterests.getOrDefault(topic, 0) + 1);
        save();
    }
    
    public static void trackHumorResponse(String player, boolean positive) {
        PlayerPatterns patterns = playerPatterns.computeIfAbsent(player, k -> new PlayerPatterns());
        if (positive) {
            patterns.humorPositive++;
        } else {
            patterns.humorNegative++;
        }
        save();
    }
    
    private static String categorizeQuestion(String question) {
        String lower = question.toLowerCase();
        if (lower.contains("how") || lower.contains("?")) return "how-to";
        if (lower.contains("what") || lower.contains("which")) return "information";
        if (lower.contains("why")) return "explanation";
        if (lower.contains("where")) return "location";
        if (lower.contains("when")) return "timing";
        if (lower.contains("should") || lower.contains("would")) return "advice";
        return "general";
    }
    
    public static String getPatternSummary(String player) {
        PlayerPatterns patterns = playerPatterns.get(player);
        if (patterns == null) {
            return "No patterns learned for " + player;
        }
        
        StringBuilder summary = new StringBuilder();
        summary.append("Patterns for ").append(player).append(":\n");
        summary.append("Total interactions: ").append(patterns.totalInteractions).append("\n");
        
        if (!patterns.questionTypes.isEmpty()) {
            String mostCommon = patterns.questionTypes.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("unknown");
            summary.append("Most asked: ").append(mostCommon).append(" questions\n");
        }
        
        if (!patterns.topicInterests.isEmpty()) {
            String favoriteTopic = patterns.topicInterests.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("unknown");
            summary.append("Favorite topic: ").append(favoriteTopic).append("\n");
        }
        
        if (!patterns.preferredLength.isEmpty()) {
            double avgLength = patterns.preferredLength.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);
            summary.append("Preferred response length: ").append(Math.round(avgLength)).append(" words\n");
        }
        
        if (patterns.humorPositive + patterns.humorNegative > 0) {
            double humorRate = (patterns.humorPositive * 100.0) / (patterns.humorPositive + patterns.humorNegative);
            summary.append("Humor appreciation: ").append(Math.round(humorRate)).append("%\n");
        }
        
        return summary.toString();
    }
    
    public static String getLearningPrompt(String player) {
        PlayerPatterns patterns = playerPatterns.get(player);
        if (patterns == null || patterns.totalInteractions < 3) {
            return "";
        }
        
        StringBuilder prompt = new StringBuilder("\n\nLEARNED PATTERNS:\n");
        
        if (!patterns.questionTypes.isEmpty()) {
            String mostCommon = patterns.questionTypes.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");
            prompt.append("User often asks ").append(mostCommon).append(" questions\n");
        }
        
        if (!patterns.topicInterests.isEmpty()) {
            String favoriteTopic = patterns.topicInterests.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");
            prompt.append("User is interested in: ").append(favoriteTopic).append("\n");
        }
        
        if (!patterns.preferredLength.isEmpty()) {
            double avgLength = patterns.preferredLength.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);
            if (avgLength < 10) {
                prompt.append("User prefers SHORT responses\n");
            } else if (avgLength > 25) {
                prompt.append("User prefers DETAILED responses\n");
            }
        }
        
        if (patterns.humorPositive + patterns.humorNegative > 5) {
            double humorRate = (patterns.humorPositive * 100.0) / (patterns.humorPositive + patterns.humorNegative);
            if (humorRate < 30) {
                prompt.append("User prefers SERIOUS tone\n");
            } else if (humorRate > 70) {
                prompt.append("User enjoys HUMOR and jokes\n");
            }
        }
        
        return prompt.toString();
    }
    
    public static int getOptimalResponseLength(String player) {
        PlayerPatterns patterns = playerPatterns.get(player);
        if (patterns == null || patterns.preferredLength.isEmpty()) {
            return 20; // default
        }
        
        return (int) patterns.preferredLength.stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(20);
    }
    
    private static class PlayerPatterns {
        Map<String, Integer> questionTypes = new HashMap<>();
        Map<String, Integer> topicInterests = new HashMap<>();
        List<ResponsePattern> successfulPatterns = new ArrayList<>();
        List<Integer> preferredLength = new ArrayList<>();
        List<Integer> conversationLengths = new ArrayList<>();
        int totalInteractions = 0;
        int humorPositive = 0;
        int humorNegative = 0;
        long lastInteraction = 0;
    }
    
    private static class ResponsePattern {
        String question;
        String response;
        long timestamp;
        
        ResponsePattern(String question, String response, long timestamp) {
            this.question = question;
            this.response = response;
            this.timestamp = timestamp;
        }
    }
}
