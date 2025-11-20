package com.aichat.gui;

import com.aichat.friends.FriendManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import java.io.IOException;

public class FriendsGui extends GuiScreen {
    private GuiScreen parentScreen;
    
    public FriendsGui(GuiScreen parent) {
        this.parentScreen = parent;
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        int centerX = this.width / 2;
        
        this.buttonList.add(new GuiButton(1, centerX - 100, 50, 200, 20, 
            "Whitelist Mode: " + (FriendManager.isWhitelistMode() ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF")));
        this.buttonList.add(new GuiButton(2, centerX - 100, 75, 200, 20, 
            "Auto-Accept: " + (FriendManager.isAutoAccept() ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF")));
        this.buttonList.add(new GuiButton(3, centerX - 100, 100, 200, 20, "View Friend List"));
        this.buttonList.add(new GuiButton(99, centerX - 100, this.height - 30, 200, 20, "Back"));
    }
    
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1:
                FriendManager.setWhitelistMode(!FriendManager.isWhitelistMode());
                button.displayString = "Whitelist Mode: " + (FriendManager.isWhitelistMode() ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF");
                break;
            case 2:
                FriendManager.setAutoAccept(!FriendManager.isAutoAccept());
                button.displayString = "Auto-Accept: " + (FriendManager.isAutoAccept() ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF");
                break;
            case 3:
                // Show friend list
                if (FriendManager.getFriends().isEmpty()) {
                    mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "No friends added."));
                } else {
                    mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Friends:"));
                    for (String friend : FriendManager.getFriends()) {
                        mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.YELLOW + "- " + friend));
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
        this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.GOLD + "" + EnumChatFormatting.BOLD + "Friends & Whitelist", this.width / 2, 15, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
