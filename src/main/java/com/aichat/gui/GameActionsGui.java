package com.aichat.gui;

import com.aichat.config.ModConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import java.io.IOException;

public class GameActionsGui extends GuiScreen {
    private GuiScreen parentScreen;
    
    public GameActionsGui(GuiScreen parent) {
        this.parentScreen = parent;
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        int centerX = this.width / 2;
        
        this.buttonList.add(new GuiButton(1, centerX - 100, 50, 200, 20, 
            "AI /play: " + (ModConfig.allowPlayCommand ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF")));
        this.buttonList.add(new GuiButton(2, centerX - 100, 75, 200, 20, 
            "AI /lobby: " + (ModConfig.allowLobbyCommand ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF")));
        this.buttonList.add(new GuiButton(3, centerX - 100, 100, 200, 20, 
            "AI /p warp: " + (ModConfig.allowWarpCommand ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF")));
        this.buttonList.add(new GuiButton(4, centerX - 100, 125, 200, 20, "View Game Modes"));
        this.buttonList.add(new GuiButton(5, centerX - 100, 150, 200, 20, "Go to Lobby"));
        this.buttonList.add(new GuiButton(99, centerX - 100, this.height - 30, 200, 20, "Back"));
    }
    
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1:
                ModConfig.allowPlayCommand = !ModConfig.allowPlayCommand;
                ModConfig.save();
                button.displayString = "AI /play: " + (ModConfig.allowPlayCommand ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF");
                break;
            case 2:
                ModConfig.allowLobbyCommand = !ModConfig.allowLobbyCommand;
                ModConfig.save();
                button.displayString = "AI /lobby: " + (ModConfig.allowLobbyCommand ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF");
                break;
            case 3:
                ModConfig.allowWarpCommand = !ModConfig.allowWarpCommand;
                ModConfig.save();
                button.displayString = "AI /p warp: " + (ModConfig.allowWarpCommand ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF");
                break;
            case 4:
                // Show game modes
                mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.GOLD + "========== Available Game Modes =========="));
                mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.GRAY + "Use /aichat game play <mode> to join:"));
                String[] games = com.aichat.hypixel.GameActionManager.getAvailableGames();
                for (String game : games) {
                    mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.YELLOW + " - " + game));
                }
                mc.displayGuiScreen(null);
                break;
            case 5:
                mc.thePlayer.sendChatMessage("/lobby");
                mc.displayGuiScreen(null);
                break;
            case 99:
                mc.displayGuiScreen(parentScreen);
                break;
        }
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.GOLD + "" + EnumChatFormatting.BOLD + "Game Actions", this.width / 2, 15, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
