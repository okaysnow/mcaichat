package com.aichat.gui;

import com.aichat.config.ModConfig;
import com.aichat.ChatHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import java.io.IOException;

public class SettingsGui extends GuiScreen {
    private GuiScreen parentScreen;
    private GuiTextField apiKeyField;
    private GuiTextField maxWordsField;
    private GuiTextField minWordsField;
    private GuiTextField delayField;
    private GuiTextField contextMessagesField;
    
    private GuiButton enabledButton;
    private GuiButton personalityButton;
    private GuiButton rememberContextButton;
    private GuiButton autoTranslateButton;
    private GuiButton silentModeButton;
    private GuiButton debugModeButton;
    
    public SettingsGui(GuiScreen parent) {
        this.parentScreen = parent;
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        
        int centerX = this.width / 2;
        int leftX = centerX - 200;
        int rightX = centerX + 10;
        int y = 40;
        
        // Text fields
        apiKeyField = new GuiTextField(0, this.fontRendererObj, leftX + 110, y, 180, 18);
        apiKeyField.setMaxStringLength(100);
        apiKeyField.setText(ModConfig.geminiApiKey.equals("your-api-key-here") ? "" : "***********");
        
        maxWordsField = new GuiTextField(1, this.fontRendererObj, leftX + 110, y + 25, 50, 18);
        maxWordsField.setMaxStringLength(3);
        maxWordsField.setText(String.valueOf(ModConfig.maxResponseWords));
        
        minWordsField = new GuiTextField(2, this.fontRendererObj, leftX + 110, y + 50, 50, 18);
        minWordsField.setMaxStringLength(3);
        minWordsField.setText(String.valueOf(ModConfig.minResponseWords));
        
        delayField = new GuiTextField(3, this.fontRendererObj, leftX + 110, y + 75, 50, 18);
        delayField.setMaxStringLength(2);
        delayField.setText(String.valueOf(ModConfig.cooldownSeconds));
        
        contextMessagesField = new GuiTextField(4, this.fontRendererObj, leftX + 110, y + 100, 50, 18);
        contextMessagesField.setMaxStringLength(2);
        contextMessagesField.setText(String.valueOf(ModConfig.maxContextMessages));
        
        // Toggle buttons
        enabledButton = new GuiButton(1, rightX, y, 180, 20, 
            "AI Responses: " + (ChatHandler.enabled ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF"));
        
        personalityButton = new GuiButton(2, rightX, y + 25, 180, 20, 
            "Personality: " + EnumChatFormatting.YELLOW + ModConfig.personality);
        
        rememberContextButton = new GuiButton(3, rightX, y + 50, 180, 20, 
            "Memory: " + (ModConfig.rememberContext ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF"));
        
        autoTranslateButton = new GuiButton(4, rightX, y + 75, 180, 20, 
            "Auto-Translate: " + (ModConfig.autoTranslate ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF"));
        
        silentModeButton = new GuiButton(5, rightX, y + 100, 180, 20, 
            "Silent Mode: " + (ModConfig.silentMode ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF"));
        
        debugModeButton = new GuiButton(6, rightX, y + 125, 180, 20, 
            "Debug Mode: " + (ChatHandler.debugMode ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF"));
        
        this.buttonList.add(enabledButton);
        this.buttonList.add(personalityButton);
        this.buttonList.add(rememberContextButton);
        this.buttonList.add(autoTranslateButton);
        this.buttonList.add(silentModeButton);
        this.buttonList.add(debugModeButton);
        
        // Save and Back buttons
        this.buttonList.add(new GuiButton(98, centerX - 105, this.height - 30, 100, 20, "Save"));
        this.buttonList.add(new GuiButton(99, centerX + 5, this.height - 30, 100, 20, "Back"));
    }
    
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1:
                ChatHandler.enabled = !ChatHandler.enabled;
                enabledButton.displayString = "AI Responses: " + (ChatHandler.enabled ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF");
                break;
            case 2:
                // Cycle through personalities
                String[] personalities = {"friendly", "sarcastic", "professional", "funny", "casual", "mocking"};
                int currentIndex = 0;
                for (int i = 0; i < personalities.length; i++) {
                    if (personalities[i].equals(ModConfig.personality)) {
                        currentIndex = i;
                        break;
                    }
                }
                ModConfig.personality = personalities[(currentIndex + 1) % personalities.length];
                personalityButton.displayString = "Personality: " + EnumChatFormatting.YELLOW + ModConfig.personality;
                break;
            case 3:
                ModConfig.rememberContext = !ModConfig.rememberContext;
                rememberContextButton.displayString = "Memory: " + (ModConfig.rememberContext ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF");
                break;
            case 4:
                ModConfig.autoTranslate = !ModConfig.autoTranslate;
                autoTranslateButton.displayString = "Auto-Translate: " + (ModConfig.autoTranslate ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF");
                break;
            case 5:
                ModConfig.silentMode = !ModConfig.silentMode;
                silentModeButton.displayString = "Silent Mode: " + (ModConfig.silentMode ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF");
                break;
            case 6:
                ChatHandler.debugMode = !ChatHandler.debugMode;
                debugModeButton.displayString = "Debug Mode: " + (ChatHandler.debugMode ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF");
                break;
            case 98:
                saveSettings();
                mc.displayGuiScreen(parentScreen);
                break;
            case 99:
                mc.displayGuiScreen(parentScreen);
                break;
        }
    }
    
    private void saveSettings() {
        try {
            if (!apiKeyField.getText().equals("***********") && !apiKeyField.getText().isEmpty()) {
                ModConfig.geminiApiKey = apiKeyField.getText();
            }
            ModConfig.maxResponseWords = Integer.parseInt(maxWordsField.getText());
            ModConfig.minResponseWords = Integer.parseInt(minWordsField.getText());
            ModConfig.cooldownSeconds = Integer.parseInt(delayField.getText());
            ModConfig.maxContextMessages = Integer.parseInt(contextMessagesField.getText());
            ModConfig.save();
        } catch (NumberFormatException e) {
            // Invalid input, don't save
        }
    }
    
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        apiKeyField.textboxKeyTyped(typedChar, keyCode);
        maxWordsField.textboxKeyTyped(typedChar, keyCode);
        minWordsField.textboxKeyTyped(typedChar, keyCode);
        delayField.textboxKeyTyped(typedChar, keyCode);
        contextMessagesField.textboxKeyTyped(typedChar, keyCode);
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        apiKeyField.mouseClicked(mouseX, mouseY, mouseButton);
        maxWordsField.mouseClicked(mouseX, mouseY, mouseButton);
        minWordsField.mouseClicked(mouseX, mouseY, mouseButton);
        delayField.mouseClicked(mouseX, mouseY, mouseButton);
        contextMessagesField.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        
        String title = EnumChatFormatting.GOLD + "" + EnumChatFormatting.BOLD + "Core Settings";
        this.drawCenteredString(this.fontRendererObj, title, this.width / 2, 15, 0xFFFFFF);
        
        int centerX = this.width / 2;
        int leftX = centerX - 200;
        int y = 43;
        
        // Labels for text fields
        this.drawString(this.fontRendererObj, "Gemini API Key:", leftX, y, 0xFFFFFF);
        this.drawString(this.fontRendererObj, "Max Words:", leftX, y + 25, 0xFFFFFF);
        this.drawString(this.fontRendererObj, "Min Words:", leftX, y + 50, 0xFFFFFF);
        this.drawString(this.fontRendererObj, "Response Delay:", leftX, y + 75, 0xFFFFFF);
        this.drawString(this.fontRendererObj, "Context Memory:", leftX, y + 100, 0xFFFFFF);
        
        // Draw text fields
        apiKeyField.drawTextBox();
        maxWordsField.drawTextBox();
        minWordsField.drawTextBox();
        delayField.drawTextBox();
        contextMessagesField.drawTextBox();
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
    public void updateScreen() {
        apiKeyField.updateCursorCounter();
        maxWordsField.updateCursorCounter();
        minWordsField.updateCursorCounter();
        delayField.updateCursorCounter();
        contextMessagesField.updateCursorCounter();
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
