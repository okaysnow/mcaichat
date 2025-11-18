package com.aichat.features;
import com.aichat.config.ModConfig;
import net.minecraft.client.Minecraft;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
public class ConversationStarter {
    private static Map<String, Long> lastSeenTime = new HashMap<>();
    private static Map<String, Boolean> hasGreeted = new HashMap<>();
    private static final long GREETING_COOLDOWN = 3600000;
    private static final long IDLE_THRESHOLD = 600000;
    private static final Random random = new Random();
    public static void trackPlayerActivity(String player) {
        if (!ModConfig.conversationStarters) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        Long lastSeen = lastSeenTime.get(player);
        if (lastSeen != null) {
            long timeSinceLastSeen = currentTime - lastSeen;
            if (timeSinceLastSeen > IDLE_THRESHOLD && !hasGreeted.getOrDefault(player, false)) {
                if (random.nextDouble() < 0.3) {
                    hasGreeted.put(player, true);
                }
            }
        }
        lastSeenTime.put(player, currentTime);
    }
    public static String checkForStarter(String player) {
        if (!ModConfig.conversationStarters) {
            return null;
        }
        if (hasGreeted.getOrDefault(player, false)) {
            hasGreeted.put(player, false);
            String[] greetings = {
                "Hey " + player + "! Haven't seen you in a while. How's it going?",
                player + "! Welcome back! What have you been up to?",
                "Oh hey " + player + ", long time no see! Everything good?",
                player + "! Good to see you around. Been busy?",
                "Hey " + player + "! You've been quiet. What's new?"
            };
            return greetings[random.nextInt(greetings.length)];
        }
        return null;
    }
    public static String generateAchievementResponse(String player, String achievement) {
        if (!ModConfig.conversationStarters) {
            return null;
        }
        String[] responses = {
            "Nice work on " + achievement + ", " + player + "!",
            "Congrats " + player + "! " + achievement + " is awesome!",
            player + " just got " + achievement + "! Well done!",
            "Impressive! " + player + " earned " + achievement + "!",
            "Way to go " + player + "! " + achievement + " looks great!"
        };
        return responses[random.nextInt(responses.length)];
    }
    public static void resetGreetingCooldown(String player) {
        hasGreeted.remove(player);
    }
    public static String getContextPrompt() {
        if (!ModConfig.conversationStarters) {
            return "";
        }
        return "\n[CONVERSATION STYLE: You can initiate conversations naturally. Feel free to greet players who return, congratulate achievements, or comment on interesting events. Be conversational and engaging.]";
    }
}
