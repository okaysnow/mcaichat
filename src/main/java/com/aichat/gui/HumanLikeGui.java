package com.aichat.gui;

import com.aichat.config.ModConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import java.io.IOException;

public class HumanLikeGui extends GuiScreen {
    private GuiScreen parentScreen;
    private GuiButton typingDelayButton, randomDelayButton, typosButton, slangButton, casualToneButton;
    
    public HumanLikeGui(GuiScreen parent) {
        this.parentScreen = parent;
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        int centerX = this.width / 2;
        int y = 50;
        
        typingDelayButton = new GuiButton(1, centerX - 100, y, 200, 20, 
            "Typing Delay: " + (ModConfig.typingDelay ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF"));
        randomDelayButton = new GuiButton(2, centerX - 100, y + 25, 200, 20, 
            "Random Delays: " + (ModConfig.randomDelay ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF"));
        typosButton = new GuiButton(3, centerX - 100, y + 50, 200, 20, 
            "Natural Typos: " + (ModConfig.naturalTypos ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF"));
        slangButton = new GuiButton(4, centerX - 100, y + 75, 200, 20, 
            "Gaming Slang: " + (ModConfig.useSlang ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF"));
        casualToneButton = new GuiButton(5, centerX - 100, y + 100, 200, 20, 
            "Casual Tone: " + (ModConfig.casualTone ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF"));
        
        this.buttonList.add(typingDelayButton);
        this.buttonList.add(randomDelayButton);
        this.buttonList.add(typosButton);
        this.buttonList.add(slangButton);
        this.buttonList.add(casualToneButton);
        this.buttonList.add(new GuiButton(98, centerX - 105, this.height - 30, 100, 20, "Save"));
        this.buttonList.add(new GuiButton(99, centerX + 5, this.height - 30, 100, 20, "Back"));
    }
    
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1:
                ModConfig.typingDelay = !ModConfig.typingDelay;
                typingDelayButton.displayString = "Typing Delay: " + (ModConfig.typingDelay ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF");
                break;
            case 2:
                ModConfig.randomDelay = !ModConfig.randomDelay;
                randomDelayButton.displayString = "Random Delays: " + (ModConfig.randomDelay ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF");
                break;
            case 3:
                ModConfig.naturalTypos = !ModConfig.naturalTypos;
                typosButton.displayString = "Natural Typos: " + (ModConfig.naturalTypos ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF");
                break;
            case 4:
                ModConfig.useSlang = !ModConfig.useSlang;
                slangButton.displayString = "Gaming Slang: " + (ModConfig.useSlang ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF");
                break;
            case 5:
                ModConfig.casualTone = !ModConfig.casualTone;
                casualToneButton.displayString = "Casual Tone: " + (ModConfig.casualTone ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF");
                break;
            case 98:
                ModConfig.save();
                mc.displayGuiScreen(parentScreen);
                break;
            case 99:
                mc.displayGuiScreen(parentScreen);
                break;
        }
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.GOLD + "" + EnumChatFormatting.BOLD + "Human-Like Behavior", this.width / 2, 15, 0xFFFFFF);
        this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.GRAY + "Make AI responses feel more natural and human", this.width / 2, 30, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
