package com.aichat.features;

import com.aichat.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.concurrent.TimeUnit;

public class RateLimitMonitor {
    
    private static long hourStartTime = System.currentTimeMillis();
    private static int responsesThisHour = 0;
    private static boolean warningShown = false;
    
    public static void trackResponse() {
        checkHourReset();
        responsesThisHour++;
        
        checkWarningThreshold();
    }
    
    private static void checkHourReset() {
        long currentTime = System.currentTimeMillis();
        long hoursSinceStart = TimeUnit.MILLISECONDS.toHours(currentTime - hourStartTime);
        
        if (hoursSinceStart >= 1) {
            hourStartTime = currentTime;
            responsesThisHour = 0;
            warningShown = false;
        }
    }
    
    private static void checkWarningThreshold() {
        if (ModConfig.rateLimitWarningPercent <= 0) {
            return;
        }
        
        if (warningShown) {
            return;
        }
        
        int maxResponses = ModConfig.maxResponsesPerHour;
        double currentPercent = (double) responsesThisHour / maxResponses * 100;
        
        if (currentPercent >= ModConfig.rateLimitWarningPercent) {
            showWarning(responsesThisHour, maxResponses);
            warningShown = true;
        }
    }
    
    private static void showWarning(int current, int max) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            String prefix = EnumChatFormatting.GOLD.toString() + EnumChatFormatting.BOLD + "[AI Chat Warning] " + EnumChatFormatting.RESET;
            String message = EnumChatFormatting.YELLOW + "You've used " + current + "/" + max + " responses this hour (" + 
                           String.format("%.0f", (double) current / max * 100) + "%). " +
                           "Approaching rate limit!";
            
            mc.thePlayer.addChatMessage(new ChatComponentText(prefix + message));
            
            long timeUntilReset = getTimeUntilReset();
            String resetTime = formatTime(timeUntilReset);
            String resetMsg = EnumChatFormatting.GRAY + "Rate limit resets in: " + EnumChatFormatting.WHITE + resetTime;
            mc.thePlayer.addChatMessage(new ChatComponentText("  " + resetMsg));
            
            String tipMsg = EnumChatFormatting.GRAY + "Tip: Use " + EnumChatFormatting.WHITE + "/aichat ratelimit <number>" + 
                          EnumChatFormatting.GRAY + " to increase the hourly limit.";
            mc.thePlayer.addChatMessage(new ChatComponentText("  " + tipMsg));
        }
    }
    
    public static long getTimeUntilReset() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - hourStartTime;
        long oneHour = TimeUnit.HOURS.toMillis(1);
        return oneHour - elapsedTime;
    }
    
    private static String formatTime(long milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(minutes);
        
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    public static int getCurrentUsage() {
        checkHourReset();
        return responsesThisHour;
    }
    
    public static double getCurrentPercentage() {
        checkHourReset();
        return (double) responsesThisHour / ModConfig.maxResponsesPerHour * 100;
    }
    
    public static String getStatusMessage() {
        checkHourReset();
        
        int max = ModConfig.maxResponsesPerHour;
        double percent = getCurrentPercentage();
        
        EnumChatFormatting color;
        if (percent >= 90) {
            color = EnumChatFormatting.RED;
        } else if (percent >= ModConfig.rateLimitWarningPercent) {
            color = EnumChatFormatting.YELLOW;
        } else {
            color = EnumChatFormatting.GREEN;
        }
        
        String status = color.toString() + responsesThisHour + "/" + max + " (" + String.format("%.1f", percent) + "%)";
        String resetTime = formatTime(getTimeUntilReset());
        
        return status + EnumChatFormatting.GRAY + " - Resets in: " + EnumChatFormatting.WHITE + resetTime;
    }
    
    public static void reset() {
        hourStartTime = System.currentTimeMillis();
        responsesThisHour = 0;
        warningShown = false;
    }
}
