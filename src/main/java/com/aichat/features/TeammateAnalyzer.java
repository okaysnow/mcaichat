package com.aichat.features;

import java.util.HashMap;
import java.util.Map;

public class TeammateAnalyzer {
    
    private static final Map<String, PlayerStats> playerStats = new HashMap<>();
    
    public static void trackPartyMember(String player) {
        if (!playerStats.containsKey(player)) {
            playerStats.put(player, new PlayerStats(player));
        }
    }
    
    public static void recordActivity(String player, ActivityType type) {
        PlayerStats stats = playerStats.get(player);
        if (stats != null) {
            stats.recordActivity(type);
        }
    }
    
    public static String analyzeTeammate(String player) {
        PlayerStats stats = playerStats.get(player);
        if (stats == null) {
            return player + " - No data yet. Play together to build profile.";
        }
        
        StringBuilder analysis = new StringBuilder();
        analysis.append("[TEAMMATE ANALYSIS: ").append(player).append("]\n");
        
        int totalActivities = stats.getTotalActivities();
        if (totalActivities == 0) {
            return player + " - Just joined party, no activity data yet.";
        }
        
        analysis.append("Activity Level: ").append(getActivityLevel(stats.messagesSent)).append("\n");
        analysis.append("Combat Style: ").append(getCombatStyle(stats)).append("\n");
        analysis.append("Team Player: ").append(getTeamScore(stats)).append("/10\n");
        analysis.append("Strengths: ").append(getStrengths(stats)).append("\n");
        
        return analysis.toString();
    }
    
    private static String getActivityLevel(int messages) {
        if (messages > 50) return "Very Active";
        if (messages > 20) return "Active";
        if (messages > 5) return "Moderate";
        return "Quiet";
    }
    
    private static String getCombatStyle(PlayerStats stats) {
        if (stats.kills > stats.deaths * 2) return "Aggressive attacker";
        if (stats.deaths < 5 && stats.kills > 0) return "Defensive player";
        if (stats.assists > stats.kills) return "Support focused";
        return "Balanced";
    }
    
    private static int getTeamScore(PlayerStats stats) {
        int score = 5;
        if (stats.assists > 10) score += 2;
        if (stats.messagesSent > 20) score += 1;
        if (stats.helpfulActions > 5) score += 2;
        return Math.min(10, score);
    }
    
    private static String getStrengths(PlayerStats stats) {
        if (stats.kills > 15) return "Strong killer";
        if (stats.assists > 15) return "Team support";
        if (stats.messagesSent > 30) return "Good communication";
        if (stats.helpfulActions > 10) return "Helpful teammate";
        return "Still learning playstyle";
    }
    
    public static String getContextPrompt() {
        if (playerStats.isEmpty()) return "";
        
        StringBuilder prompt = new StringBuilder("\n[PARTY MEMBERS DATA]\n");
        for (PlayerStats stats : playerStats.values()) {
            if (stats.getTotalActivities() > 0) {
                prompt.append(stats.player).append(": ")
                     .append("K:").append(stats.kills).append(" D:").append(stats.deaths)
                     .append(" A:").append(stats.assists).append(" Msgs:").append(stats.messagesSent)
                     .append("\n");
            }
        }
        return prompt.toString();
    }
    
    public static void clearStats(String player) {
        playerStats.remove(player);
    }
    
    public static void clearAllStats() {
        playerStats.clear();
    }
    
    public enum ActivityType {
        KILL, DEATH, ASSIST, MESSAGE, HELPFUL_ACTION
    }
    
    private static class PlayerStats {
        String player;
        int kills = 0;
        int deaths = 0;
        int assists = 0;
        int messagesSent = 0;
        int helpfulActions = 0;
        
        PlayerStats(String player) {
            this.player = player;
        }
        
        void recordActivity(ActivityType type) {
            switch (type) {
                case KILL: kills++; break;
                case DEATH: deaths++; break;
                case ASSIST: assists++; break;
                case MESSAGE: messagesSent++; break;
                case HELPFUL_ACTION: helpfulActions++; break;
            }
        }
        
        int getTotalActivities() {
            return kills + deaths + assists + messagesSent + helpfulActions;
        }
    }
}
