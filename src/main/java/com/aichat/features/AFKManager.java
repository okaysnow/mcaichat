package com.aichat.features;
public class AFKManager {
    private static boolean isAFK = false;
    private static long lastActivityTime = System.currentTimeMillis();
    private static long afkThreshold = 300000;
    private static boolean autoAFK = true;
    private static String afkPersonality = "minimal";
    public static void updateActivity() {
        if (isAFK && autoAFK) {
            isAFK = false;
            System.out.println("[AI Chat] AFK mode disabled - player is active");
        }
        lastActivityTime = System.currentTimeMillis();
    }
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
    public static void setAFK(boolean afk) {
        isAFK = afk;
        if (afk) {
            System.out.println("[AI Chat] AFK mode manually enabled");
        } else {
            lastActivityTime = System.currentTimeMillis();
            System.out.println("[AI Chat] AFK mode manually disabled");
        }
    }
    public static boolean isAFK() {
        return checkAFK();
    }
    public static void setAFKThreshold(int minutes) {
        afkThreshold = minutes * 60000L;
    }
    public static void setAutoAFK(boolean auto) {
        autoAFK = auto;
    }
    public static String getAFKPersonality() {
        return afkPersonality;
    }
    public static void setAFKPersonality(String personality) {
        afkPersonality = personality;
    }
    public static String modifyPromptForAFK(String originalPrompt) {
        if (!isAFK) return originalPrompt;
        return originalPrompt + "\n\nIMPORTANT: The user is currently AFK (away from keyboard). " +
               "Keep your response very brief (1-5 words) and informative. " +
               "Let them know you'll give a full response when they return if needed. " +
               "Be understanding about delayed responses.";
    }
    public static long getTimeSinceActivity() {
        return (System.currentTimeMillis() - lastActivityTime) / 1000;
    }
    public static boolean isAutoAFKEnabled() {
        return autoAFK;
    }
}
