package com.aichat.features;

import com.aichat.config.ModConfig;

public class TypingSimulator {
    
    private static final int WORDS_PER_MINUTE = 200;
    private static final int CHARS_PER_WORD = 5;
    private static final int BASE_DELAY_MS = 500;
    
    public static long calculateTypingDelay(String message) {
        if (!ModConfig.typingDelay) {
            return 0;
        }
        
        int charCount = message.length();
        int wordCount = message.split("\\s+").length;
        
        long msPerChar = (60000 / (WORDS_PER_MINUTE * CHARS_PER_WORD));
        long typingTime = charCount * msPerChar;
        
        long thinkingTime = wordCount > 10 ? 1000 : 500;
        
        return BASE_DELAY_MS + thinkingTime + typingTime;
    }
    
    public static String formatHypixelSafe(String message) {
        message = message.replaceAll("\\*\\*([^*]+)\\*\\*", "$1");
        message = message.replaceAll("\\*([^*]+)\\*", "$1");
        message = message.replaceAll("__([^_]+)__", "$1");
        message = message.replaceAll("_([^_]+)_", "$1");
        message = message.replaceAll("~~([^~]+)~~", "$1");
        message = message.replaceAll("`([^`]+)`", "$1");
        message = message.replaceAll("\\[([^\\]]+)\\]\\([^)]+\\)", "$1");
        message = message.replaceAll("^#+\\s+", "");
        message = message.replaceAll("!\\[([^\\]]+)\\]\\([^)]+\\)", "");
        message = message.replaceAll("<[^>]+>", "");
        message = message.replaceAll("&[a-fklmnor0-9]", "");
        message = message.replaceAll("§[a-fklmnor0-9]", "");
        
        return message.trim();
    }
}
