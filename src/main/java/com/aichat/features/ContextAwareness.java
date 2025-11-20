package com.aichat.features;

import java.util.regex.Pattern;

public class ContextAwareness {
    
    private static String currentGameMode = "unknown";
    private static String currentLocation = "unknown";
    private static long lastUpdateTime = System.currentTimeMillis();
    
    private static final Pattern GAME_MODE_PATTERN = Pattern.compile(
        "(?i)(?:joined|playing|in)\\s+(bed\\s?wars?|sky\\s?wars?|duels?|sky\\s?block|murder\\s?mystery|build\\s?battle|uhc|arcade|mega\\s?walls?|blitz|cops\\s?and\\s?crims|smash\\s?heroes|turbo\\s?kart|tnt\\s?games|lobby)"
    );
    
    private static final Pattern LOCATION_PATTERN = Pattern.compile(
        "(?i)(?:at|in|near|by)\\s+(spawn|base|mid|middle|shop|forge|bridge|island|lobby)"
    );
    
    public static void detectGameMode(String message) {
        java.util.regex.Matcher matcher = GAME_MODE_PATTERN.matcher(message);
        if (matcher.find()) {
            String mode = matcher.group(1).toLowerCase()
                .replaceAll("\\s+", "");
            setGameMode(mode);
        }
    }
    
    public static void detectLocation(String message) {
        java.util.regex.Matcher matcher = LOCATION_PATTERN.matcher(message);
        if (matcher.find()) {
            String location = matcher.group(1).toLowerCase();
            setLocation(location);
        }
    }
    
    public static void setGameMode(String mode) {
        currentGameMode = mode;
        lastUpdateTime = System.currentTimeMillis();
    }
    
    public static void setLocation(String location) {
        currentLocation = location;
        lastUpdateTime = System.currentTimeMillis();
    }
    
    public static String getCurrentGameMode() {
        return currentGameMode;
    }
    
    public static String getCurrentLocation() {
        return currentLocation;
    }
    
    public static String getContextPrompt() {
        if (currentGameMode.equals("unknown") && currentLocation.equals("unknown")) {
            return "";
        }
        
        StringBuilder prompt = new StringBuilder("\n[CONTEXT AWARENESS]\n");
        
        if (!currentGameMode.equals("unknown")) {
            prompt.append("Game Mode: ").append(currentGameMode).append("\n");
            prompt.append(getGameModeAdvice(currentGameMode));
        }
        
        if (!currentLocation.equals("unknown")) {
            prompt.append("Location: ").append(currentLocation).append("\n");
            prompt.append(getLocationAdvice(currentLocation));
        }
        
        long timeSinceUpdate = (System.currentTimeMillis() - lastUpdateTime) / 60000;
        if (timeSinceUpdate > 10) {
            prompt.append("(Context may be outdated - last update ").append(timeSinceUpdate).append(" mins ago)\n");
        }
        
        return prompt.toString();
    }
    
    private static String getGameModeAdvice(String mode) {
        switch (mode.toLowerCase()) {
            case "bedwars":
                return "Strategy: Protect bed, gather resources, rush enemy bases\n";
            case "skywars":
                return "Strategy: Loot chests quickly, bridge carefully, control center\n";
            case "duels":
                return "Strategy: PvP focused, learn combos, practice timing\n";
            case "skyblock":
                return "Strategy: Grind resources, optimize farms, manage economy\n";
            case "murdermystery":
                return "Strategy: Detective finds clues, innocents survive, murderer stays hidden\n";
            default:
                return "";
        }
    }
    
    private static String getLocationAdvice(String location) {
        switch (location.toLowerCase()) {
            case "spawn":
            case "base":
                return "Advice: Safe zone, good for planning\n";
            case "mid":
            case "middle":
                return "Advice: Danger zone, high risk high reward\n";
            case "shop":
                return "Advice: Buy upgrades, prepare for battle\n";
            case "bridge":
                return "Advice: Vulnerable crossing point\n";
            default:
                return "";
        }
    }
    
    public static void reset() {
        currentGameMode = "unknown";
        currentLocation = "unknown";
        lastUpdateTime = System.currentTimeMillis();
    }
}
