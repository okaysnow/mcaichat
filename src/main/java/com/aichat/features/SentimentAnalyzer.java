package com.aichat.features;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SentimentAnalyzer {
    
    private static final Pattern POSITIVE_WORDS = Pattern.compile("(?i)\\b(love|like|amazing|awesome|great|excellent|wonderful|fantastic|good|nice|happy|thanks|thank you|appreciate|helpful|perfect|best|cool|fun|enjoy|excited|glad)\\b");
    private static final Pattern NEGATIVE_WORDS = Pattern.compile("(?i)\\b(hate|dislike|terrible|awful|bad|worst|horrible|stupid|dumb|annoying|angry|mad|sad|upset|disappointed|useless|suck|trash|garbage|disgusting)\\b");
    private static final Pattern STRONG_POSITIVE = Pattern.compile("(?i)\\b(incredible|outstanding|phenomenal|brilliant|magnificent|superb|exceptional|flawless|perfect|legendary)\\b");
    private static final Pattern STRONG_NEGATIVE = Pattern.compile("(?i)\\b(horrible|terrible|disgusting|pathetic|abysmal|atrocious|despicable|vile|miserable|horrendous)\\b");
    
    private static final Pattern EXCITED_PUNCTUATION = Pattern.compile("!{2,}");
    private static final Pattern QUESTION_MARKS = Pattern.compile("\\?{2,}");
    private static final Pattern ALL_CAPS = Pattern.compile("\\b[A-Z]{4,}\\b");
    
    public static SentimentResult analyze(String message) {
        if (message == null || message.isEmpty()) {
            return new SentimentResult(SentimentType.NEUTRAL, 0.0, "Empty message");
        }
        
        double score = 0.0;
        StringBuilder reasons = new StringBuilder();
        
        int positiveMatches = countMatches(POSITIVE_WORDS, message);
        int negativeMatches = countMatches(NEGATIVE_WORDS, message);
        int strongPositiveMatches = countMatches(STRONG_POSITIVE, message);
        int strongNegativeMatches = countMatches(STRONG_NEGATIVE, message);
        
        score += positiveMatches * 0.5;
        score -= negativeMatches * 0.5;
        score += strongPositiveMatches * 1.0;
        score -= strongNegativeMatches * 1.0;
        
        if (positiveMatches > 0) reasons.append("positive words, ");
        if (negativeMatches > 0) reasons.append("negative words, ");
        if (strongPositiveMatches > 0) reasons.append("strong positive language, ");
        if (strongNegativeMatches > 0) reasons.append("strong negative language, ");
        
        if (EXCITED_PUNCTUATION.matcher(message).find()) {
            score += 0.2;
            reasons.append("excitement, ");
        }
        
        if (QUESTION_MARKS.matcher(message).find()) {
            score -= 0.1;
            reasons.append("confusion, ");
        }
        
        if (ALL_CAPS.matcher(message).find()) {
            score += (score > 0 ? 0.3 : -0.3);
            reasons.append("emphasis, ");
        }
        
        score = Math.max(-5.0, Math.min(5.0, score));
        
        SentimentType type;
        if (score >= 1.5) {
            type = SentimentType.VERY_POSITIVE;
        } else if (score >= 0.5) {
            type = SentimentType.POSITIVE;
        } else if (score <= -1.5) {
            type = SentimentType.VERY_NEGATIVE;
        } else if (score <= -0.5) {
            type = SentimentType.NEGATIVE;
        } else {
            type = SentimentType.NEUTRAL;
        }
        
        String reasonStr = reasons.length() > 0 ? reasons.substring(0, reasons.length() - 2) : "neutral tone";
        
        return new SentimentResult(type, score, reasonStr);
    }
    
    private static int countMatches(Pattern pattern, String text) {
        java.util.regex.Matcher matcher = pattern.matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }
    
    public static String getPersonalityAdjustment(SentimentType sentiment) {
        switch (sentiment) {
            case VERY_POSITIVE:
                return "\n[SENTIMENT: User is very happy/excited. Match their energy with enthusiasm!]";
            case POSITIVE:
                return "\n[SENTIMENT: User is in a good mood. Be friendly and positive.]";
            case VERY_NEGATIVE:
                return "\n[SENTIMENT: User is upset/angry. Be empathetic and supportive.]";
            case NEGATIVE:
                return "\n[SENTIMENT: User seems frustrated. Be understanding and helpful.]";
            case NEUTRAL:
            default:
                return "\n[SENTIMENT: Neutral tone. Respond normally.]";
        }
    }
    
    public enum SentimentType {
        VERY_POSITIVE,
        POSITIVE,
        NEUTRAL,
        NEGATIVE,
        VERY_NEGATIVE
    }
    
    public static class SentimentResult {
        public final SentimentType type;
        public final double score;
        public final String reasons;
        
        public SentimentResult(SentimentType type, double score, String reasons) {
            this.type = type;
            this.score = score;
            this.reasons = reasons;
        }
        
        @Override
        public String toString() {
            return String.format("%s (%.2f): %s", type, score, reasons);
        }
    }
}
