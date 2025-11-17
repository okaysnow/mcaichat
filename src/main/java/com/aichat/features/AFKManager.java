package com.aichat.features;

public class AFKManager {
    
    private static boolean isAFK = false;
    private static long lastActivityTime = System.currentTimeMillis();
    private static long afkThreshold = 300000;
    private static boolean autoAFK = true;
    private static String afkPersonality = "minimal";
    
    /**
     * Update player activity (called on movement, chat, etc.)
     */
    public static void updateActivity() {
        if (isAFK && autoAFK) {

            isAFK = false;
            System.out.println("[AI Chat] AFK mode disabled - player is active");
        }
        lastActivityTime = System.currentTimeMillis();
    }
    
    /**
     * Check if player should be considered AFK
     * @return true if player is AFK
     */
    public static boolean checkAFK() {
        if (!autoAFK) return isAFK;
        
        long timeSinceActivity = System.currentTimeMillis() - lastActivityTime;
        boolean shouldBeAFK = timeSinceActivity >= afkThreshold;
        
        if (shouldBeAFK && !isAFK) {
            isAFK = true;
            System.out.println("[AI Chat] AFK mode enabled - no activity for " + (timeSinceActivity / 1000) + " seconds");
        }
        
        return isAFK;
    }
    
    /**
     * Manually set AFK status
     * @param afk true to enable AFK mode
     */
    public static void setAFK(boolean afk) {
        isAFK = afk;
        if (afk) {
            System.out.println("[AI Chat] AFK mode manually enabled");
        } else {
            lastActivityTime = System.currentTimeMillis();
            System.out.println("[AI Chat] AFK mode manually disabled");
        }
    }
    
    /**
     * Check if player is currently AFK
     * @return true if AFK
     */
    public static boolean isAFK() {
        return checkAFK();
    }
    
    /**
     * Set AFK threshold time
     * @param minutes Minutes of inactivity before AFK
     */
    public static void setAFKThreshold(int minutes) {
        afkThreshold = minutes * 60000L;
    }
    
    /**
     * Set whether to automatically detect AFK
     * @param auto true to enable auto-detection
     */
    public static void setAutoAFK(boolean auto) {
        autoAFK = auto;
    }
    
    /**
     * Get AFK personality mode
     * @return Personality type for AFK mode
     */
    public static String getAFKPersonality() {
        return afkPersonality;
    }
    
    /**
     * Set AFK personality mode
     * @param personality Personality type
     */
    public static void setAFKPersonality(String personality) {
        afkPersonality = personality;
    }
    
    /**
     * Modify AI prompt based on AFK status
     * @param originalPrompt The original prompt
     * @return Modified prompt
     */
    public static String modifyPromptForAFK(String originalPrompt) {
        if (!isAFK) return originalPrompt;
        
        return originalPrompt + "\n\nIMPORTANT: The user is currently AFK (away from keyboard). " +
               "Keep your response very brief (1-5 words) and informative. " +
               "Let them know you'll give a full response when they return if needed. " +
               "Be understanding about delayed responses.";
    }
    
    /**
     * Get time since last activity in seconds
     * @return Seconds since last activity
     */
    public static long getTimeSinceActivity() {
        return (System.currentTimeMillis() - lastActivityTime) / 1000;
    }
    
    /**
     * Check if auto-AFK is enabled
     * @return true if auto-AFK is enabled
     */
    public static boolean isAutoAFKEnabled() {
        return autoAFK;
    }
}
