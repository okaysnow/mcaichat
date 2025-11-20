package com.aichat.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import java.io.IOException;

public class PartyGui extends GuiScreen {
    private GuiScreen parentScreen;
    
    public PartyGui(GuiScreen parent) {
        this.parentScreen = parent;
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        int centerX = this.width / 2;
        
        this.buttonList.add(new GuiButton(1, centerX - 100, 50, 200, 20, "View Party Status"));
        this.buttonList.add(new GuiButton(2, centerX - 100, 75, 200, 20, "Warp Party"));
        this.buttonList.add(new GuiButton(3, centerX - 100, 100, 200, 20, "Leave Party"));
        this.buttonList.add(new GuiButton(4, centerX - 100, 125, 200, 20, "Auto-Invite Settings"));
        this.buttonList.add(new GuiButton(99, centerX - 100, this.height - 30, 200, 20, "Back"));
    }
    
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1:
                // Show party status
                if (!com.aichat.hypixel.PartyManager.isInParty()) {
                    mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.GRAY + "Not in a party"));
                } else {
                    mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.GOLD + "========== Party Status =========="));
                    mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.WHITE + "Party Leader: " + (com.aichat.hypixel.PartyManager.isPartyLeader() ? EnumChatFormatting.GREEN + "Yes" : EnumChatFormatting.RED + "No")));
                    mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.WHITE + "Party Size: " + EnumChatFormatting.YELLOW + com.aichat.hypixel.PartyManager.getPartySize()));
                    mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.WHITE + "Members:"));
                    for (String member : com.aichat.hypixel.PartyManager.getPartyMembers()) {
                        mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.YELLOW + " - " + member));
                    }
                }
                mc.displayGuiScreen(null);
                break;
            case 2:
                // Warp party
                if (!com.aichat.hypixel.PartyManager.isPartyLeader()) {
                    mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.RED + "You must be party leader to warp"));
                } else if (!com.aichat.config.ModConfig.allowWarpCommand) {
                    mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.RED + "Party warp is disabled. Enable in Game Actions"));
                } else {
                    com.aichat.hypixel.PartyManager.warpParty();
                    mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.GREEN + "Warping party..."));
                }
                mc.displayGuiScreen(null);
                break;
            case 3:
                // Leave party
                com.aichat.hypixel.PartyManager.leaveParty();
                mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.GREEN + "Left party"));
                mc.displayGuiScreen(null);
                break;
            case 4:
                // Toggle auto-invite
                com.aichat.config.ModConfig.autoInviteToParty = !com.aichat.config.ModConfig.autoInviteToParty;
                com.aichat.config.ModConfig.save();
                String status = com.aichat.config.ModConfig.autoInviteToParty ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled";
                mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Auto-invite " + status));
                break;
            case 99:
                mc.displayGuiScreen(parentScreen);
                break;
        }
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.GOLD + "" + EnumChatFormatting.BOLD + "Party Management", this.width / 2, 15, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
