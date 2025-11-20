package com.aichat.gui;

import com.aichat.config.ModConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import java.io.IOException;

public class AIChatGui extends GuiScreen {
    private GuiScreen parentScreen;
    
    // Category buttons
    private GuiButton settingsButton;
    private GuiButton friendsButton;
    private GuiButton partyButton;
    private GuiButton memoryButton;
    private GuiButton statsButton;
    private GuiButton gameButton;
    private GuiButton humanLikeButton;
    private GuiButton quickActionsButton;
    
    public AIChatGui(GuiScreen parent) {
        this.parentScreen = parent;
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        
        int centerX = this.width / 2;
        int startY = 40;
        int buttonWidth = 200;
        int buttonHeight = 20;
        int spacing = 25;
        
        // Title area
        
        // Category buttons (left column)
        int leftX = centerX - buttonWidth - 10;
        settingsButton = new GuiButton(1, leftX, startY, buttonWidth, buttonHeight, "Settings");
        humanLikeButton = new GuiButton(2, leftX, startY + spacing, buttonWidth, buttonHeight, "Human-Like Behavior");
        friendsButton = new GuiButton(3, leftX, startY + spacing * 2, buttonWidth, buttonHeight, "Friends & Whitelist");
        partyButton = new GuiButton(4, leftX, startY + spacing * 3, buttonWidth, buttonHeight, "Party Management");
        
        // Right column
        int rightX = centerX + 10;
        memoryButton = new GuiButton(5, rightX, startY, buttonWidth, buttonHeight, "Memory & Learning");
        statsButton = new GuiButton(6, rightX, startY + spacing, buttonWidth, buttonHeight, "Statistics");
        gameButton = new GuiButton(7, rightX, startY + spacing * 2, buttonWidth, buttonHeight, "Game Actions");
        quickActionsButton = new GuiButton(8, rightX, startY + spacing * 3, buttonWidth, buttonHeight, "Quick Actions");
        
        this.buttonList.add(settingsButton);
        this.buttonList.add(humanLikeButton);
        this.buttonList.add(friendsButton);
        this.buttonList.add(partyButton);
        this.buttonList.add(memoryButton);
        this.buttonList.add(statsButton);
        this.buttonList.add(gameButton);
        this.buttonList.add(quickActionsButton);
        
        // Back button at bottom
        this.buttonList.add(new GuiButton(99, centerX - 100, this.height - 30, 200, 20, "Close"));
    }
    
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1:
                mc.displayGuiScreen(new SettingsGui(this));
                break;
            case 2:
                mc.displayGuiScreen(new HumanLikeGui(this));
                break;
            case 3:
                mc.displayGuiScreen(new FriendsGui(this));
                break;
            case 4:
                mc.displayGuiScreen(new PartyGui(this));
                break;
            case 5:
                mc.displayGuiScreen(new MemoryGui(this));
                break;
            case 6:
                mc.displayGuiScreen(new StatsGui(this));
                break;
            case 7:
                mc.displayGuiScreen(new GameActionsGui(this));
                break;
            case 8:
                mc.displayGuiScreen(new QuickActionsGui(this));
                break;
            case 99:
                mc.displayGuiScreen(parentScreen);
                break;
        }
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        
        // Title
        String title = EnumChatFormatting.GOLD + "" + EnumChatFormatting.BOLD + "AI Chat Bot Configuration";
        this.drawCenteredString(this.fontRendererObj, title, this.width / 2, 15, 0xFFFFFF);
        
        // Status indicator
        String status = com.aichat.ChatHandler.enabled ? 
            EnumChatFormatting.GREEN + "Enabled" : 
            EnumChatFormatting.RED + "Disabled";
        this.drawString(this.fontRendererObj, "Status: " + status, 10, 10, 0xFFFFFF);
        
        // API status
        String apiStatus = ModConfig.geminiApiKey.equals("your-api-key-here") ? 
            EnumChatFormatting.RED + "Not configured" : 
            EnumChatFormatting.GREEN + "Configured";
        this.drawString(this.fontRendererObj, "API: " + apiStatus, 10, 22, 0xFFFFFF);
        
        super.drawScreen(mouseX, mouseY, partialTicks);
        
        // Tooltips
        if (settingsButton.isMouseOver()) {
            this.drawHoveringText(java.util.Arrays.asList("Configure core settings, personality, delays"), mouseX, mouseY);
        } else if (humanLikeButton.isMouseOver()) {
            this.drawHoveringText(java.util.Arrays.asList("Typing delays, typos, slang, casual tone"), mouseX, mouseY);
        } else if (friendsButton.isMouseOver()) {
            this.drawHoveringText(java.util.Arrays.asList("Manage friend list and whitelist mode"), mouseX, mouseY);
        } else if (partyButton.isMouseOver()) {
            this.drawHoveringText(java.util.Arrays.asList("Party invites, kicks, warp, auto-actions"), mouseX, mouseY);
        } else if (memoryButton.isMouseOver()) {
            this.drawHoveringText(java.util.Arrays.asList("Conversation memory, patterns, learning"), mouseX, mouseY);
        } else if (statsButton.isMouseOver()) {
            this.drawHoveringText(java.util.Arrays.asList("Performance statistics and analytics"), mouseX, mouseY);
        } else if (gameButton.isMouseOver()) {
            this.drawHoveringText(java.util.Arrays.asList("Game mode commands and permissions"), mouseX, mouseY);
        } else if (quickActionsButton.isMouseOver()) {
            this.drawHoveringText(java.util.Arrays.asList("Toggle, mute, emergency stop, test API"), mouseX, mouseY);
        }
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
