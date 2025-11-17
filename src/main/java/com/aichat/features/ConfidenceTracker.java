package com.aichat.features;

import com.aichat.config.ModConfig;
import net.minecraft.util.EnumChatFormatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfidenceTracker {
    
    private static final Pattern CONFIDENCE_PATTERN = Pattern.compile("CONFIDENCE:(\\d+)%");
    private static final Pattern UNCERTAIN_WORDS = Pattern.compile("(?i)(maybe|perhaps|possibly|might|could be|not sure|think|probably|guess)");
    
    public static String getSystemPrompt() {
        if (!ModConfig.showConfidence) {
            return "";
        }
        
        return "\n[CONFIDENCE SCORING: When answering questions, include your confidence level as CONFIDENCE:XX% at the end of your response (where XX is 0-100). Base this on:\n" +
               "- 90-100%: Factual, verifiable information you're certain about\n" +
               "- 70-89%: High confidence but some uncertainty remains\n" +
               "- 50-69%: Moderate confidence, educated guess\n" +
               "- 30-49%: Low confidence, speculative\n" +
               "- 0-29%: Very uncertain or don't know\n" +
               "Include CONFIDENCE:XX% at the very end of your response.]";
    }
    
    public static String formatResponseWithConfidence(String response) {
        if (!ModConfig.showConfidence) {
            return response;
        }
        
        Matcher matcher = CONFIDENCE_PATTERN.matcher(response);
        
        if (matcher.find()) {
            String confidenceStr = matcher.group(1);
            int confidence = Integer.parseInt(confidenceStr);
            
            String cleanResponse = response.replaceAll("CONFIDENCE:\\d+%", "").trim();
            
            String badge = getConfidenceBadge(confidence);
            String prefix = "";
            
            if (confidence < 50) {
                prefix = EnumChatFormatting.GRAY + "[Low Confidence] " + EnumChatFormatting.RESET;
            } else if (confidence < 70) {
                prefix = EnumChatFormatting.YELLOW + "[Moderate Confidence] " + EnumChatFormatting.RESET;
            } else if (confidence < 90) {
                prefix = EnumChatFormatting.GREEN + "[High Confidence] " + EnumChatFormatting.RESET;
            } else {
                prefix = EnumChatFormatting.DARK_GREEN + "[Very Confident] " + EnumChatFormatting.RESET;
            }
            
            return prefix + cleanResponse + " " + badge;
        }
        
        int estimatedConfidence = estimateConfidence(response);
        
        if (estimatedConfidence < 70) {
            String badge = getConfidenceBadge(estimatedConfidence);
            String prefix = EnumChatFormatting.YELLOW + "[Estimated Confidence] " + EnumChatFormatting.RESET;
            return prefix + response + " " + badge;
        }
        
        return response;
    }
    
    private static int estimateConfidence(String response) {
        Matcher uncertainMatcher = UNCERTAIN_WORDS.matcher(response);
        int uncertainCount = 0;
        
        while (uncertainMatcher.find()) {
            uncertainCount++;
        }
        
        if (response.contains("I don't know") || response.contains("I'm not sure")) {
            return 20;
        }
        
        if (uncertainCount >= 3) {
            return 40;
        } else if (uncertainCount == 2) {
            return 55;
        } else if (uncertainCount == 1) {
            return 70;
        }
        
        return 85;
    }
    
    private static String getConfidenceBadge(int confidence) {
        EnumChatFormatting color;
        String text;
        
        if (confidence >= 90) {
            color = EnumChatFormatting.DARK_GREEN;
            text = confidence + "%";
        } else if (confidence >= 70) {
            color = EnumChatFormatting.GREEN;
            text = confidence + "%";
        } else if (confidence >= 50) {
            color = EnumChatFormatting.YELLOW;
            text = confidence + "%";
        } else if (confidence >= 30) {
            color = EnumChatFormatting.GOLD;
            text = confidence + "%";
        } else {
            color = EnumChatFormatting.RED;
            text = confidence + "%";
        }
        
        return color + "(" + text + ")" + EnumChatFormatting.RESET;
    }
}
