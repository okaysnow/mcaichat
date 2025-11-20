package com.aichat.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import java.io.IOException;

public class StatsGui extends GuiScreen {
    private GuiScreen parentScreen;
    
    public StatsGui(GuiScreen parent) {
        this.parentScreen = parent;
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        int centerX = this.width / 2;
        
        this.buttonList.add(new GuiButton(1, centerX - 100, 50, 200, 20, "Performance Stats"));
        this.buttonList.add(new GuiButton(2, centerX - 100, 75, 200, 20, "Rate Limit Status"));
        this.buttonList.add(new GuiButton(3, centerX - 100, 100, 200, 20, "Teammate Analysis"));
        this.buttonList.add(new GuiButton(99, centerX - 100, this.height - 30, 200, 20, "Back"));
    }
    
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1:
                // Show performance stats
                long avgTime = com.aichat.analytics.ResponseTimeTracker.getAverageResponseTime();
                long fastest = com.aichat.analytics.ResponseTimeTracker.getFastestTime();
                long slowest = com.aichat.analytics.ResponseTimeTracker.getSlowestTime();
                int total = com.aichat.analytics.ResponseTimeTracker.getTotalResponses();
                int failed = com.aichat.analytics.ResponseTimeTracker.getFailedResponses();
                double successRate = total > 0 ? ((total - failed) * 100.0 / total) : 100.0;
                
                mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.GOLD + "========== AI Chat Statistics =========="));
                mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.WHITE + "Total Responses: " + EnumChatFormatting.YELLOW + total));
                mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.WHITE + "Failed Responses: " + EnumChatFormatting.YELLOW + failed));
                mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.WHITE + "Success Rate: " + EnumChatFormatting.YELLOW + String.format("%.1f%%", successRate)));
                mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.WHITE + "Avg Response Time: " + EnumChatFormatting.YELLOW + avgTime + "ms"));
                mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.WHITE + "Fastest Response: " + EnumChatFormatting.YELLOW + fastest + "ms"));
                mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.WHITE + "Slowest Response: " + EnumChatFormatting.YELLOW + slowest + "ms"));
                mc.displayGuiScreen(null);
                break;
            case 2:
                // Show rate limit status
                mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Rate Limit Status:"));
                mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText("  " + com.aichat.features.RateLimitMonitor.getStatusMessage()));
                mc.displayGuiScreen(null);
                break;
            case 3:
                mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.GRAY + "Teammate stats tracked in party chat"));
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
        this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.GOLD + "" + EnumChatFormatting.BOLD + "Statistics", this.width / 2, 15, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
