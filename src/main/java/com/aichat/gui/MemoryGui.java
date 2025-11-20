package com.aichat.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import java.io.IOException;

public class MemoryGui extends GuiScreen {
    private GuiScreen parentScreen;
    
    public MemoryGui(GuiScreen parent) {
        this.parentScreen = parent;
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        int centerX = this.width / 2;
        
        this.buttonList.add(new GuiButton(1, centerX - 100, 50, 200, 20, "View Learned Patterns"));
        this.buttonList.add(new GuiButton(2, centerX - 100, 75, 200, 20, "Memory Statistics"));
        this.buttonList.add(new GuiButton(3, centerX - 100, 100, 200, 20, "Save Memory"));
        this.buttonList.add(new GuiButton(4, centerX - 100, 125, 200, 20, "Clear My Memory"));
        this.buttonList.add(new GuiButton(5, centerX - 100, 150, 200, 20, "Active Conversations"));
        this.buttonList.add(new GuiButton(99, centerX - 100, this.height - 30, 200, 20, "Back"));
    }
    
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1:
                // Show patterns for current player
                String playerName = mc.getSession().getUsername();
                String summary = com.aichat.features.PatternLearning.getPatternSummary(playerName);
                // Display in chat
                for (String line : summary.split("\n")) {
                    mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.GRAY + line));
                }
                mc.displayGuiScreen(null);
                break;
            case 2:
                // Show memory stats
                java.util.Map<String, Integer> stats = com.aichat.context.MemoryPersistence.getMemoryStats();
                mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.GOLD + "========== Memory Statistics =========="));
                mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.WHITE + "Players in memory: " + EnumChatFormatting.YELLOW + stats.size()));
                int total = 0;
                for (int count : stats.values()) {
                    total += count;
                }
                mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.WHITE + "Total messages: " + EnumChatFormatting.YELLOW + total));
                mc.displayGuiScreen(null);
                break;
            case 3:
                // Save memory
                com.aichat.context.MemoryPersistence.saveMemory();
                mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.GREEN + "Memory saved!"));
                break;
            case 4:
                // Clear memory for current player
                String player = mc.getSession().getUsername();
                com.aichat.context.MemoryPersistence.clearMemory(player);
                mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.GREEN + "Cleared memory for " + player));
                break;
            case 5:
                // Show active conversations
                java.util.Map<String, Long> windows = com.aichat.context.ConversationWindow.getActiveWindows();
                if (windows.isEmpty()) {
                    mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.GRAY + "No active conversation windows"));
                } else {
                    mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.GOLD + "========== Active Conversations =========="));
                    for (java.util.Map.Entry<String, Long> entry : windows.entrySet()) {
                        long remaining = com.aichat.context.ConversationWindow.getTimeRemaining(entry.getKey());
                        long seconds = remaining / 1000;
                        long minutes = seconds / 60;
                        seconds = seconds % 60;
                        mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(
                            EnumChatFormatting.YELLOW + entry.getKey() + EnumChatFormatting.GRAY + " - " + 
                            EnumChatFormatting.WHITE + minutes + "m " + seconds + "s remaining"
                        ));
                    }
                }
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
        this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.GOLD + "" + EnumChatFormatting.BOLD + "Memory & Learning", this.width / 2, 15, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
