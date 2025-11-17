package com.aichat.hypixel;

import com.aichat.config.ModConfig;
import net.minecraft.client.Minecraft;

public class GameActionManager {
    
    private static boolean allowPlayCommand = false;
    private static boolean allowLobbyCommand = false;
    private static boolean allowWarpCommand = false;
    
    public static void playGame(String gameMode) {
        if (!allowPlayCommand) {
            System.out.println("[AI Chat] Play command is disabled");
            return;
        }
        
        Minecraft.getMinecraft().addScheduledTask(() -> {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/play " + gameMode);
        });
        System.out.println("[AI Chat] Joining game: " + gameMode);
    }
    
    public static void goToLobby() {
        if (!allowLobbyCommand) {
            System.out.println("[AI Chat] Lobby command is disabled");
            return;
        }
        
        Minecraft.getMinecraft().addScheduledTask(() -> {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/lobby");
        });
        System.out.println("[AI Chat] Returning to lobby");
    }
    
    public static void warpToHub() {
        if (!allowWarpCommand) {
            System.out.println("[AI Chat] Warp command is disabled");
            return;
        }
        
        Minecraft.getMinecraft().addScheduledTask(() -> {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/lobby");
        });
        System.out.println("[AI Chat] Warping to hub");
    }
    
    public static String[] getAvailableGames() {
        return new String[]{
            "bedwars_eight_one",
            "bedwars_eight_two",
            "bedwars_four_four",
            "skywars_solo_normal",
            "skywars_solo_insane",
            "skywars_teams_normal",
            "skywars_teams_insane",
            "duels_bridge_duel",
            "duels_uhc_duel",
            "duels_sumo_duel",
            "arcade_mini_walls",
            "arcade_party_games",
            "murder_mystery",
            "build_battle",
            "skyblock"
        };
    }
    
    public static boolean isValidGameMode(String mode) {
        for (String game : getAvailableGames()) {
            if (game.equalsIgnoreCase(mode)) {
                return true;
            }
        }
        return false;
    }
    
    public static void setAllowPlayCommand(boolean allow) {
        allowPlayCommand = allow;
        ModConfig.allowPlayCommand = allow;
        ModConfig.save();
    }
    
    public static boolean isPlayCommandAllowed() {
        return allowPlayCommand;
    }
    
    public static void setAllowLobbyCommand(boolean allow) {
        allowLobbyCommand = allow;
        ModConfig.allowLobbyCommand = allow;
        ModConfig.save();
    }
    
    public static boolean isLobbyCommandAllowed() {
        return allowLobbyCommand;
    }
    
    public static void setAllowWarpCommand(boolean allow) {
        allowWarpCommand = allow;
        ModConfig.allowWarpCommand = allow;
        ModConfig.save();
    }
    
    public static boolean isWarpCommandAllowed() {
        return allowWarpCommand;
    }
    
    public static void loadFromConfig() {
        allowPlayCommand = ModConfig.allowPlayCommand;
        allowLobbyCommand = ModConfig.allowLobbyCommand;
        allowWarpCommand = ModConfig.allowWarpCommand;
    }
}
