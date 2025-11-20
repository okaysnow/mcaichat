package com.aichat.gui;

import com.aichat.config.ModConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import java.io.IOException;

public class QuickActionsGui extends GuiScreen {
    private GuiScreen parentScreen;
    
    public QuickActionsGui(GuiScreen parent) {
        this.parentScreen = parent;
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        
        int centerX = this.width / 2;
        int y = 50;
        int buttonWidth = 200;
        
        this.buttonList.add(new GuiButton(1, centerX - 100, y, buttonWidth, 20, 
            com.aichat.ChatHandler.enabled ? "Mute AI" : "Unmute AI"));
        this.buttonList.add(new GuiButton(2, centerX - 100, y + 25, buttonWidth, 20, "Test API"));
        this.buttonList.add(new GuiButton(3, centerX - 100, y + 50, buttonWidth, 20, "Emergency Stop"));
        this.buttonList.add(new GuiButton(4, centerX - 100, y + 75, buttonWidth, 20, "Wipe All Data"));
        this.buttonList.add(new GuiButton(5, centerX - 100, y + 100, buttonWidth, 20, "Save Memory Now"));
        this.buttonList.add(new GuiButton(6, centerX - 100, y + 125, buttonWidth, 20, "Rate Limit Status"));
        
        this.buttonList.add(new GuiButton(99, centerX - 100, this.height - 30, 200, 20, "Back"));
    }
    
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1:
                com.aichat.ChatHandler.enabled = !com.aichat.ChatHandler.enabled;
                ModConfig.save();
                button.displayString = com.aichat.ChatHandler.enabled ? "Mute AI" : "Unmute AI";
                break;
            case 2:
                // Test API - show message and close GUI
                mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Testing Gemini API..."));
                mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.GRAY + "Check chat for results (this may take a moment)"));
                mc.thePlayer.sendChatMessage("/aichat testapi");
                mc.displayGuiScreen(null);
                break;
            case 3:
                // Emergency stop - close GUI first, then send message
                com.aichat.ChatHandler.enabled = false;
                com.aichat.ChatHandler.debugMode = false;
                ModConfig.silentMode = false;
                ModConfig.save();
                mc.displayGuiScreen(null);
                mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.RED + "" + EnumChatFormatting.BOLD + "EMERGENCY STOP - All AI disabled"));
                break;
            case 4:
                com.aichat.context.MemoryPersistence.clearAllMemory();
                com.aichat.context.ConversationManager.clearAll();
                com.aichat.features.TeammateAnalyzer.clearAllStats();
                mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.GREEN + "All AI data wiped"));
                mc.displayGuiScreen(null);
                break;
            case 5:
                com.aichat.context.MemoryPersistence.saveMemory();
                mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.GREEN + "Memory saved"));
                break;
            case 6:
                // Show rate limit status
                mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Rate Limit Status:"));
                mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText("  " + com.aichat.features.RateLimitMonitor.getStatusMessage()));
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
        
        String title = EnumChatFormatting.GOLD + "" + EnumChatFormatting.BOLD + "Quick Actions";
        this.drawCenteredString(this.fontRendererObj, title, this.width / 2, 15, 0xFFFFFF);
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
