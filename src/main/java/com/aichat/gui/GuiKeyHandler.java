package com.aichat.gui;

import com.aichat.gui.AIChatGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class GuiKeyHandler {
    private static final KeyBinding GUI_KEY = new KeyBinding("Open AI Chat GUI", Keyboard.KEY_RSHIFT, "AI Chat");
    
    public static void register() {
        ClientRegistry.registerKeyBinding(GUI_KEY);
    }
    
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (GUI_KEY.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new AIChatGui(null));
        }
    }
}
