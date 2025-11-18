package com.aichat.hypixel;
import com.aichat.config.ModConfig;
import java.util.HashMap;
import java.util.Map;
public class PartyLeaderAssistant {
    private static long lastSuggestionTime = 0;
    private static final long SUGGESTION_COOLDOWN = 120000;
    private static final long IDLE_THRESHOLD = 180000;
    private static Map<String, Long> lastActivity = new HashMap<>();
    private static long becameLeaderTime = 0;
    private static boolean hasWarnedIdle = false;
    public static void onBecameLeader() {
        becameLeaderTime = System.currentTimeMillis();
        hasWarnedIdle = false;
        lastActivity.clear();
        for (String member : PartyManager.getPartyMembers()) {
            lastActivity.put(member, System.currentTimeMillis());
        }
    }
    public static void onMemberActivity(String player) {
        lastActivity.put(player, System.currentTimeMillis());
    }
    public static String checkForSuggestions() {
        if (!PartyManager.isPartyLeader()) {
            return null;
        }
        if (System.currentTimeMillis() - lastSuggestionTime < SUGGESTION_COOLDOWN) {
            return null;
        }
        int partySize = PartyManager.getPartySize();
        if (partySize < 2) {
            return null;
        }
        long currentTime = System.currentTimeMillis();
        long leaderTime = currentTime - becameLeaderTime;
        if (leaderTime > 30000 && !hasWarnedIdle) {
            int idleCount = 0;
            for (Long activity : lastActivity.values()) {
                if (currentTime - activity > IDLE_THRESHOLD) {
                    idleCount++;
                }
            }
            if (idleCount >= partySize * 0.5 && partySize >= 3) {
                hasWarnedIdle = true;
                lastSuggestionTime = currentTime;
                if (ModConfig.allowWarpCommand) {
                    return "Half your party seems idle. Should I warp everyone to get their attention? (I can use /p warp)";
                } else {
                    return "Half your party seems idle for 3+ minutes. You might want to use /aichat game togglewarp to let me help coordinate.";
                }
            }
        }
        if (leaderTime > 60000 && partySize >= 2) {
            lastSuggestionTime = currentTime;
            String suggestion = getSuggestedGameMode(partySize);
            if (suggestion != null) {
                if (ModConfig.allowPlayCommand) {
                    return "Your party of " + partySize + " has been ready for a while. " + suggestion;
                } else {
                    return "Your party of " + partySize + " seems ready to play. Enable /aichat game toggleplay to let me queue you up.";
                }
            }
        }
        return null;
    }
    private static String getSuggestedGameMode(int partySize) {
        switch (partySize) {
            case 2:
                return "Want to try Bed Wars Doubles (bedwars_eight_two) or Bridge Duels (duels_bridge_duel)?";
            case 3:
                return "Perfect size for Murder Mystery or SkyWars Trios!";
            case 4:
                return "You have exactly 4 players - ideal for Bed Wars 4v4 (bedwars_four_four)!";
            case 5:
            case 6:
            case 7:
            case 8:
                return "Large party! How about Murder Mystery or TNT Games?";
            default:
                if (partySize > 8) {
                    return "Huge party! Consider splitting up or trying party games.";
                }
                return null;
        }
    }
    public static String getContextPrompt() {
        if (!PartyManager.isPartyLeader() || PartyManager.getPartySize() < 2) {
            return "";
        }
        int partySize = PartyManager.getPartySize();
        long leaderTime = System.currentTimeMillis() - becameLeaderTime;
        StringBuilder context = new StringBuilder();
        context.append("\n[PARTY STATUS: You are the party leader with ").append(partySize).append(" members. ");
        if (leaderTime > 60000) {
            context.append("Party has been formed for ").append(leaderTime / 1000).append(" seconds. ");
        }
        int idleCount = 0;
        long currentTime = System.currentTimeMillis();
        for (Long activity : lastActivity.values()) {
            if (currentTime - activity > IDLE_THRESHOLD) {
                idleCount++;
            }
        }
        if (idleCount > 0) {
            context.append(idleCount).append(" member(s) appear idle. ");
        }
        if (ModConfig.allowWarpCommand) {
            context.append("You can warp the party with /p warp if needed. ");
        }
        if (ModConfig.allowPlayCommand) {
            context.append("You can queue for games. Suggest appropriate game modes based on party size.]");
        } else {
            context.append("]");
        }
        return context.toString();
    }
}
