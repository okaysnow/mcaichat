package com.aichat;
import com.aichat.config.ModConfig;
import com.aichat.friends.FriendManager;
import com.aichat.friends.FriendRequestHandler;
import com.aichat.hypixel.PartyManager;
import com.aichat.hypixel.GameActionManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.ClientCommandHandler;
@Mod(modid = AIChatMod.MODID, version = AIChatMod.VERSION, name = AIChatMod.NAME, clientSideOnly = true)
public class AIChatMod {
    public static final String MODID = "aichat";
    public static final String NAME = "AI Chat Mod";
    public static final String VERSION = "1.0";
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        System.out.println("AI Chat Mod: Pre-initialization");
        ModConfig.load(event.getModConfigurationDirectory());
        FriendManager.load(event.getModConfigurationDirectory());
    }
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        System.out.println("AI Chat Mod: Initialization");
        MinecraftForge.EVENT_BUS.register(new ChatHandler());
        MinecraftForge.EVENT_BUS.register(new FriendRequestHandler());
        MinecraftForge.EVENT_BUS.register(new PartyManager());
        GameActionManager.loadFromConfig();
        ClientCommandHandler.instance.registerCommand(new AIChatCommand());
    }
}
