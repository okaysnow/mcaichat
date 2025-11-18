package com.aichat.hypixel;
import com.aichat.config.ModConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
public class SmartInvites {
    private static final Pattern PLAY_TOGETHER_PATTERN = Pattern.compile(
        ".*(let'?s play|wanna play|want to play|down to play|play together|party up|team up|join me|play with).*",
        Pattern.CASE_INSENSITIVE
    );
    private static final Pattern GAME_QUESTION_PATTERN = Pattern.compile(
        ".*(what game|which game|what should we play|where should we play|wanna queue|ready to queue).*",
        Pattern.CASE_INSENSITIVE
    );
    private static final Pattern INVITE_REQUEST_PATTERN = Pattern.compile(
        ".*(invite me|add me to party|can i join|party invite|send invite).*",
        Pattern.CASE_INSENSITIVE
    );
    private static Map<String, Long> inviteSuggestions = new HashMap<>();
    private static Map<String, Integer> conversationScores = new HashMap<>();
    private static final long SUGGESTION_EXPIRY = 300000;
    private static final int SCORE_THRESHOLD = 3;
    public static void analyzeMessage(String player, String message) {
        if (player == null || message == null) {
            return;
        }
        message = message.toLowerCase();
        if (INVITE_REQUEST_PATTERN.matcher(message).matches()) {
            conversationScores.put(player, SCORE_THRESHOLD + 2);
            return;
        }
        int score = conversationScores.getOrDefault(player, 0);
        if (PLAY_TOGETHER_PATTERN.matcher(message).matches()) {
            score += 2;
        } else if (GAME_QUESTION_PATTERN.matcher(message).matches()) {
            score += 1;
        } else if (message.contains("party") || message.contains("team")) {
            score += 1;
        }
        if (score > 0) {
            conversationScores.put(player, score);
        }
    }
    public static String checkForInviteSuggestion(String player) {
        if (!ModConfig.autoInviteToParty) {
            return null;
        }
        if (PartyManager.getPartyMembers().contains(player)) {
            return null;
        }
        int score = conversationScores.getOrDefault(player, 0);
        if (score < SCORE_THRESHOLD) {
            return null;
        }
        Long lastSuggestion = inviteSuggestions.get(player);
        if (lastSuggestion != null && System.currentTimeMillis() - lastSuggestion < SUGGESTION_EXPIRY) {
            return null;
        }
        inviteSuggestions.put(player, System.currentTimeMillis());
        conversationScores.put(player, 0);
        if (!PartyManager.isInParty()) {
            return "It seems like " + player + " wants to play together. Should I invite them to a party? (Auto-invite is enabled)";
        } else {
            int partySize = PartyManager.getPartySize();
            if (partySize >= 8) {
                return player + " wants to join, but the party is full (8/8).";
            }
            return player + " seems interested in playing. Should I send them a party invite? (Party: " + partySize + "/8)";
        }
    }
    public static boolean shouldAutoInvite(String player) {
        if (!ModConfig.autoInviteToParty) {
            return false;
        }
        if (PartyManager.getPartyMembers().contains(player)) {
            return false;
        }
        int score = conversationScores.getOrDefault(player, 0);
        return score >= SCORE_THRESHOLD + 2;
    }
    public static void executeAutoInvite(String player) {
        if (shouldAutoInvite(player)) {
            if (!PartyManager.isInParty()) {
                PartyManager.invitePlayer(player);
                conversationScores.put(player, 0);
            } else if (PartyManager.getPartySize() < 8) {
                PartyManager.invitePlayer(player);
                conversationScores.put(player, 0);
            }
        }
    }
    public static String getContextPrompt(String player) {
        int score = conversationScores.getOrDefault(player, 0);
        if (score >= SCORE_THRESHOLD) {
            StringBuilder context = new StringBuilder();
            context.append("\n[SOCIAL CONTEXT: ").append(player);
            context.append(" has shown interest in playing together (score: ").append(score).append("). ");
            if (ModConfig.autoInviteToParty) {
                if (!PartyManager.isInParty()) {
                    context.append("You can suggest inviting them to a party. ");
                } else if (PartyManager.getPartySize() < 8) {
                    context.append("You can suggest adding them to your current party (")
                           .append(PartyManager.getPartySize()).append("/8). ");
                } else {
                    context.append("Party is full (8/8), cannot invite. ");
                }
            } else {
                context.append("Auto-invite is disabled, but you could mention party play if appropriate. ");
            }
            context.append("]");
            return context.toString();
        }
        return "";
    }
    public static void clearScore(String player) {
        conversationScores.remove(player);
        inviteSuggestions.remove(player);
    }
    public static void decayScores() {
        long currentTime = System.currentTimeMillis();
        inviteSuggestions.entrySet().removeIf(entry -> 
            currentTime - entry.getValue() > SUGGESTION_EXPIRY
        );
        for (String player : conversationScores.keySet()) {
            int score = conversationScores.get(player);
            if (score > 0) {
                conversationScores.put(player, score - 1);
            }
        }
        conversationScores.entrySet().removeIf(entry -> entry.getValue() <= 0);
    }
}
