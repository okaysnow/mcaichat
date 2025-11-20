package com.aichat.features;

import com.aichat.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashMap;
import java.util.Map;

public class EventTriggers {
    
    private static boolean enabled = true;
    private static final Map<EventType, Long> lastTriggerTime = new HashMap<>();
    private static final long COOLDOWN_MS = 30000;
    
    private static int killStreak = 0;
    private static int deathStreak = 0;
    private static int winStreak = 0;
    private static int lossStreak = 0;
    
    public static void triggerEvent(EventType event, String context) {
        if (!enabled || !canTrigger(event)) return;
        
        String response = null;
        boolean isMocking = com.aichat.config.ModConfig.personality.equalsIgnoreCase("mocking");
        
        switch (event) {
            case KILL:
                killStreak++;
                deathStreak = 0;
                if (isMocking) {
                    if (killStreak == 3) response = "only 3 kills? thats kinda mid ngl";
                    else if (killStreak == 5) response = "5 kills wow ur on fire... against literal bots lmao";
                    else if (killStreak >= 10) response = "ok ok " + killStreak + " kills, guess ur not completely trash";
                } else {
                    if (killStreak == 3) response = "Triple kill! You're on fire!";
                    else if (killStreak == 5) response = "Killing spree! Dominating!";
                    else if (killStreak >= 10) response = "Unstoppable! " + killStreak + " kills!";
                }
                break;
                
            case DEATH:
                deathStreak++;
                killStreak = 0;
                if (isMocking) {
                    if (deathStreak == 3) response = "died 3 times already? bro just uninstall lmao";
                    else if (deathStreak >= 5) response = "" + deathStreak + " deaths omg ur actually terrible at this game";
                    else response = "lmaoo how did u even die there, thats embarassing";
                } else {
                    if (deathStreak == 3) response = "Tough game. Want to switch strategies?";
                    else if (deathStreak >= 5) response = "Having a rough time. Take a break?";
                }
                break;
                
            case WIN:
                winStreak++;
                lossStreak = 0;
                if (isMocking) {
                    if (winStreak == 3) response = "3 wins? wow u finally figured out waht ur doing";
                    else if (winStreak == 5) response = "5 wins in a row, didnt know u had it in u tbh";
                    else response = "u won? must be ur lucky day lol";
                } else {
                    if (winStreak == 3) response = "3 wins in a row! Nice streak!";
                    else if (winStreak == 5) response = "5 win streak! You're crushing it!";
                    else response = "Victory! Well played!";
                }
                break;
                
            case LOSS:
                lossStreak++;
                winStreak = 0;
                if (isMocking) {
                    if (lossStreak == 2) response = "2 losses in a row? yikes bro";
                    else if (lossStreak >= 3) response = "" + lossStreak + " losses, maybe try a different game lmao";
                    else response = "L, better luck next time... or not";
                } else {
                    if (lossStreak == 2) response = "Close one! Almost had it";
                    else if (lossStreak >= 3) response = "Tough losses. Keep trying, you got this!";
                }
                break;
                
            case LEVEL_UP:
                if (isMocking) {
                    response = "finally leveled up? took u long enough";
                } else {
                    response = "Level up! Nice progress!";
                }
                break;
                
            case ACHIEVEMENT:
                if (isMocking) {
                    response = "wow an achievement, u want a cookie for that?";
                } else {
                    response = "Achievement unlocked! " + context;
                }
                break;
                
            case BED_DESTROYED:
                if (isMocking) {
                    response = "ur bed got destroyed lol, nice defense";
                } else {
                    response = "Bed destroyed! Time to go on the offensive!";
                }
                break;
                
            case FINAL_KILL:
                if (isMocking) {
                    response = "final kill? ok i guess thats not terrible";
                } else {
                    response = "Final kill! Eliminated them for good!";
                }
                break;
                
            case GAME_START:
                killStreak = 0;
                deathStreak = 0;
                if (isMocking) {
                    response = "new game, try not to throw this time";
                } else {
                    response = "Game starting! Good luck have fun!";
                }
                break;
        }
        
        if (response != null && !ModConfig.silentMode) {
            lastTriggerTime.put(event, System.currentTimeMillis());
            sendTriggerMessage(response);
        }
    }
    
    private static boolean canTrigger(EventType event) {
        Long lastTime = lastTriggerTime.get(event);
        if (lastTime == null) return true;
        
        return System.currentTimeMillis() - lastTime > COOLDOWN_MS;
    }
    
    private static void sendTriggerMessage(String message) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(
                new ChatComponentText(EnumChatFormatting.GOLD + "[AI] " + EnumChatFormatting.WHITE + message)
            );
        }
    }
    
    public static void setEnabled(boolean enable) {
        enabled = enable;
    }
    
    public static boolean isEnabled() {
        return enabled;
    }
    
    public static void resetStreaks() {
        killStreak = 0;
        deathStreak = 0;
        winStreak = 0;
        lossStreak = 0;
    }
    
    public static String getStreakInfo() {
        StringBuilder info = new StringBuilder();
        if (killStreak > 0) info.append("Kill streak: ").append(killStreak).append("\n");
        if (deathStreak > 0) info.append("Death streak: ").append(deathStreak).append("\n");
        if (winStreak > 0) info.append("Win streak: ").append(winStreak).append("\n");
        if (lossStreak > 0) info.append("Loss streak: ").append(lossStreak).append("\n");
        return info.length() > 0 ? info.toString() : "No active streaks";
    }
    
    public enum EventType {
        KILL, DEATH, WIN, LOSS, LEVEL_UP, ACHIEVEMENT, BED_DESTROYED, FINAL_KILL, GAME_START
    }
}
