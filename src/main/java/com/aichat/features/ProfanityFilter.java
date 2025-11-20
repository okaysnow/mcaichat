package com.aichat.features;

import java.util.regex.Pattern;

public class ProfanityFilter {
    
    private static final Pattern TOXIC_PATTERNS = Pattern.compile(
        "(?i)\\b(trash|garbage|terrible|awful|suck|useless|worst|pathetic|idiot|stupid|dumb|loser)\\b"
    );
    
    private static final Pattern INSULT_PATTERNS = Pattern.compile(
        "(?i)\\b(you suck|ur bad|you're bad|get good|skill issue|uninstall|quit|delete game)\\b"
    );
    
    private static final Pattern AGGRESSIVE_PATTERNS = Pattern.compile(
        "(?i)\\b(kill yourself|kys|die|hate you|annoying|cringe)\\b"
    );
    
    public static boolean isToxic(String message) {
        return TOXIC_PATTERNS.matcher(message).find() || 
               INSULT_PATTERNS.matcher(message).find() || 
               AGGRESSIVE_PATTERNS.matcher(message).find();
    }
    
    public static String getPoliteResponse() {
        String[] responses = {
            "I understand you're frustrated, but let's keep it friendly",
            "Hey, let's stay positive! We're all here to have fun",
            "I get it, but maybe we can phrase that more constructively?",
            "Let's keep the chat friendly for everyone",
            "I hear you're upset. Want to talk about what's bothering you?",
            "Let's focus on having a good time together",
            "I appreciate the passion, but let's keep it respectful"
        };
        return responses[(int)(Math.random() * responses.length)];
    }
    
    public static String getContextPrompt(String message) {
        if (isToxic(message)) {
            return "\n[TOXICITY DETECTED: This message contains negative language. Respond calmly and try to de-escalate. Be understanding but don't encourage toxicity. Suggest positive alternatives.]";
        }
        return "";
    }
    
    public static String sanitizeForAI(String message) {
        String sanitized = message;
        
        sanitized = AGGRESSIVE_PATTERNS.matcher(sanitized).replaceAll("[HOSTILE LANGUAGE]");
        sanitized = INSULT_PATTERNS.matcher(sanitized).replaceAll("[NEGATIVE COMMENT]");
        
        return sanitized;
    }
}
