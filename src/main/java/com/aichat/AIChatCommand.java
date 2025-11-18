package com.aichat;
import com.aichat.config.ModConfig;
import com.aichat.features.RateLimitMonitor;
import com.aichat.friends.FriendManager;
import com.aichat.hypixel.PartyManager;
import com.aichat.hypixel.GameActionManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
public class AIChatCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "aichat";
    }
    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/aichat <help|toggle|personality|delay|friend|guild|visual|set|addtrigger|removetrigger|listtriggers|config|stats|learn|afk>";
    }
    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            showHelpMenu(sender);
            return;
        }
        switch (args[0].toLowerCase()) {
            case "help":
                showHelpMenu(sender);
                break;
            case "toggle":
                ChatHandler.enabled = !ChatHandler.enabled;
                String status = ChatHandler.enabled ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled";
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "AI responses are now " + status));
                ModConfig.save();
                break;
            case "personality":
                if (args.length < 2) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Current personality: " + EnumChatFormatting.YELLOW + ModConfig.personality));
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Available: friendly, sarcastic, professional, funny, casual, mocking"));
                    return;
                }
                String newPersonality = args[1].toLowerCase();
                if (newPersonality.equals("friendly") || newPersonality.equals("sarcastic") || 
                    newPersonality.equals("professional") || newPersonality.equals("funny") || 
                    newPersonality.equals("casual") || newPersonality.equals("mocking")) {
                    ModConfig.personality = newPersonality;
                    ModConfig.save();
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Personality set to: " + EnumChatFormatting.YELLOW + newPersonality));
                } else {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Invalid personality. Choose: friendly, sarcastic, professional, funny, casual, mocking"));
                }
                break;
            case "addtrigger":
                if (args.length < 2) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat addtrigger <text>"));
                    return;
                }
                String addTrigger = joinArgs(args, 1);
                ChatHandler.customTriggers.add(addTrigger.toLowerCase());
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Added trigger: " + EnumChatFormatting.YELLOW + addTrigger));
                break;
            case "removetrigger":
                if (args.length < 2) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat removetrigger <text>"));
                    return;
                }
                String removeTrigger = joinArgs(args, 1);
                if (ChatHandler.customTriggers.remove(removeTrigger.toLowerCase())) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Removed trigger: " + EnumChatFormatting.YELLOW + removeTrigger));
                } else {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Trigger not found: " + removeTrigger));
                }
                break;
            case "listtriggers":
                if (ChatHandler.customTriggers.isEmpty()) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "No custom triggers set."));
                } else {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Custom triggers:"));
                    for (String trigger : ChatHandler.customTriggers) {
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "- " + trigger));
                    }
                }
                break;
            case "delay":
                if (args.length < 2) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Current response delay: " + EnumChatFormatting.YELLOW + ModConfig.cooldownSeconds + " seconds"));
                    return;
                }
                try {
                    int delay = Integer.parseInt(args[1]);
                    if (delay < 0) {
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Delay must be 0 or greater"));
                        return;
                    }
                    if (delay > 60) {
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Delay cannot exceed 60 seconds"));
                        return;
                    }
                    ModConfig.cooldownSeconds = delay;
                    ModConfig.save();
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Response delay set to: " + EnumChatFormatting.YELLOW + delay + " seconds"));
                } catch (NumberFormatException e) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Invalid number. Usage: /aichat delay <seconds>"));
                }
                break;
            case "friend":
                handleFriendCommand(sender, args);
                break;
            case "guild":
                handleGuildCommand(sender, args);
                break;
            case "visual":
                handleVisualCommand(sender, args);
                break;
            case "set":
                handleSetCommand(sender, args);
                break;
            case "config":
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "========== AI Chat Configuration =========="));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "AI Service: " + EnumChatFormatting.YELLOW + ModConfig.aiService));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Personality: " + EnumChatFormatting.YELLOW + ModConfig.personality));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Max Words: " + EnumChatFormatting.YELLOW + ModConfig.maxResponseWords));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Response Delay: " + EnumChatFormatting.YELLOW + ModConfig.cooldownSeconds + "s"));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Context Memory: " + EnumChatFormatting.YELLOW + (ModConfig.rememberContext ? "ON" : "OFF")));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Auto-Translate: " + EnumChatFormatting.YELLOW + (ModConfig.autoTranslate ? "ON" : "OFF")));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Edit config at: config/aichat.json"));
                break;
            case "stats":
                handleStatsCommand(sender, args);
                break;
            case "learn":
                handleLearnCommand(sender, args);
                break;
            case "afk":
                handleAFKCommand(sender, args);
                break;
            case "memory":
                handleMemoryCommand(sender, args);
                break;
            case "convo":
            case "conversation":
                handleConversationCommand(sender, args);
                break;
            case "party":
                handlePartyCommand(sender, args);
                break;
            case "game":
                handleGameCommand(sender, args);
                break;
            case "mute":
                ChatHandler.enabled = false;
                ModConfig.save();
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.RED + "All AI responses muted"));
                break;
            case "unmute":
                ChatHandler.enabled = true;
                ModConfig.save();
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.GREEN + "AI responses unmuted"));
                break;
            case "debug":
                ChatHandler.debugMode = !ChatHandler.debugMode;
                String debugStatus = ChatHandler.debugMode ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled";
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Debug mode " + debugStatus));
                break;
            case "silent":
                ModConfig.silentMode = !ModConfig.silentMode;
                ModConfig.save();
                String silentStatus = ModConfig.silentMode ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled";
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Silent mode " + silentStatus));
                if (ModConfig.silentMode) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Thinking indicators and badges are now hidden"));
                }
                break;
            case "testapi":
                handleTestAPICommand(sender);
                break;
            case "ratelimitstatus":
            case "rlstatus":
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Rate Limit Status:"));
                sender.addChatMessage(new ChatComponentText("  " + RateLimitMonitor.getStatusMessage()));
                break;
            case "emergency":
                ChatHandler.enabled = false;
                ChatHandler.debugMode = false;
                ModConfig.silentMode = false;
                ModConfig.save();
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED.toString() + EnumChatFormatting.BOLD + "[AI Chat] EMERGENCY STOP ACTIVATED"));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "All AI responses disabled"));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "Use /aichat unmute to re-enable"));
                break;
            default:
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Unknown subcommand. Use /aichat help"));
                break;
        }
    }
    private void showHelpMenu(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText(""));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "==================================="));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "  " + EnumChatFormatting.AQUA + EnumChatFormatting.BOLD + "AI Chat Bot" + EnumChatFormatting.GOLD + ""));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "  " + EnumChatFormatting.GRAY + "by snow" + EnumChatFormatting.GOLD + ""));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "==================================="));
        sender.addChatMessage(new ChatComponentText(""));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Main Commands:"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat help " + EnumChatFormatting.GRAY + "- Show this help menu"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat toggle " + EnumChatFormatting.GRAY + "- Enable/disable AI responses"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat mute " + EnumChatFormatting.GRAY + "- Quick disable all responses"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat unmute " + EnumChatFormatting.GRAY + "- Quick enable all responses"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat personality <type> " + EnumChatFormatting.GRAY + "- Change AI personality"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "   Types: friendly, sarcastic, professional, funny, casual, mocking"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat delay <seconds> " + EnumChatFormatting.GRAY + "- Set response cooldown (0-60)"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat silent " + EnumChatFormatting.GRAY + "- Toggle silent mode (no indicators)"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat debug " + EnumChatFormatting.GRAY + "- Toggle debug mode (shows detailed logs)"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat testapi " + EnumChatFormatting.GRAY + "- Test AI API connection"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat ratelimitstatus " + EnumChatFormatting.GRAY + "- View current rate limit usage"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat emergency " + EnumChatFormatting.GRAY + "- Emergency stop (disable everything)"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat config " + EnumChatFormatting.GRAY + "- View current settings"));
        sender.addChatMessage(new ChatComponentText(""));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Friend Management:"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat friend add <player> " + EnumChatFormatting.GRAY + "- Add friend"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat friend remove <player> " + EnumChatFormatting.GRAY + "- Remove friend"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat friend list " + EnumChatFormatting.GRAY + "- Show all friends"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat friend whitelist " + EnumChatFormatting.GRAY + "- Toggle whitelist mode"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat friend autoaccept " + EnumChatFormatting.GRAY + "- Toggle auto-accept friend requests"));
        sender.addChatMessage(new ChatComponentText(""));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Guild & Visual:"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat guild " + EnumChatFormatting.GRAY + "- Show guild settings"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat guild toggle " + EnumChatFormatting.GRAY + "- Toggle guild chat responses"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat guild personality <type> " + EnumChatFormatting.GRAY + "- Set guild personality"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat guild mention " + EnumChatFormatting.GRAY + "- Toggle mention requirement"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat visual thinking " + EnumChatFormatting.GRAY + "- Toggle thinking indicator"));
        sender.addChatMessage(new ChatComponentText(""));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Advanced Features:"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat stats " + EnumChatFormatting.GRAY + "- View performance statistics"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat learn <key> <value> " + EnumChatFormatting.GRAY + "- Teach AI a fact"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat afk " + EnumChatFormatting.GRAY + "- View AFK status"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat afk toggle " + EnumChatFormatting.GRAY + "- Toggle AFK mode manually"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat afk auto " + EnumChatFormatting.GRAY + "- Toggle auto-AFK detection"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat afk time <mins> " + EnumChatFormatting.GRAY + "- Set AFK timeout (1-60)"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat memory " + EnumChatFormatting.GRAY + "- View memory commands"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat memory save " + EnumChatFormatting.GRAY + "- Manually save memory"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat memory clear [player] " + EnumChatFormatting.GRAY + "- Clear memory"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat memory stats " + EnumChatFormatting.GRAY + "- View memory statistics"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat convo " + EnumChatFormatting.GRAY + "- View active conversation windows"));
        sender.addChatMessage(new ChatComponentText(""));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Party Management (Hypixel):"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat party status " + EnumChatFormatting.GRAY + "- View party members"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat party invite <player> " + EnumChatFormatting.GRAY + "- Invite to party"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat party kick <player> " + EnumChatFormatting.GRAY + "- Kick from party (leader)"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat party warp " + EnumChatFormatting.GRAY + "- Warp party to your lobby"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat party leave " + EnumChatFormatting.GRAY + "- Leave party"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat party autoinvite <on|off> " + EnumChatFormatting.GRAY + "- AI auto-invites"));
        sender.addChatMessage(new ChatComponentText(""));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Game Actions (Hypixel):"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat game play <mode> " + EnumChatFormatting.GRAY + "- Join a game mode"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat game lobby " + EnumChatFormatting.GRAY + "- Return to lobby"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat game list " + EnumChatFormatting.GRAY + "- Show available game modes"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat game toggleplay " + EnumChatFormatting.GRAY + "- Toggle AI /play permission"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat game togglelobby " + EnumChatFormatting.GRAY + "- Toggle AI /lobby permission"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat game togglewarp " + EnumChatFormatting.GRAY + "- Toggle AI /p warp permission"));
        sender.addChatMessage(new ChatComponentText(""));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Trigger Management:"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat addtrigger <text> " + EnumChatFormatting.GRAY + "- Add custom trigger"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat removetrigger <text> " + EnumChatFormatting.GRAY + "- Remove trigger"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " /aichat listtriggers " + EnumChatFormatting.GRAY + "- View all triggers"));
        sender.addChatMessage(new ChatComponentText(""));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Configuration (/aichat set <option> <value>):"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + " Core: " + EnumChatFormatting.GRAY + "aiservice, geminiapikey, maxwords, minwords"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + " Context: " + EnumChatFormatting.GRAY + "contextmessages, contexttimeout, remembercontext"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + " Features: " + EnumChatFormatting.GRAY + "emotiondetection, learning, chainlimit, starters, confidence"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + " Visual: " + EnumChatFormatting.GRAY + "streaming, streamingspeed, badges, randomdelay, typingdelay"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + " Translation: " + EnumChatFormatting.GRAY + "autotranslate, targetlanguage"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + " Limits: " + EnumChatFormatting.GRAY + "maxperhour, ratelimitwarning"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Example: /aichat set maxwords 50"));
        sender.addChatMessage(new ChatComponentText(""));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_GRAY + "AI responds when your name is mentioned or custom triggers fire."));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_GRAY + "Config: config/aichat.json | Use /aichat help for this menu"));
        sender.addChatMessage(new ChatComponentText(""));
    }
    private String joinArgs(String[] args, int start) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            if (i > start) builder.append(" ");
            builder.append(args[i]);
        }
        return builder.toString();
    }
    private void handleFriendCommand(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat friend <add|remove|list|whitelist|autoaccept>"));
            return;
        }
        switch (args[1].toLowerCase()) {
            case "add":
                if (args.length < 3) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat friend add <player>"));
                    return;
                }
                FriendManager.addFriend(args[2]);
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Added friend: " + EnumChatFormatting.YELLOW + args[2]));
                break;
            case "remove":
                if (args.length < 3) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat friend remove <player>"));
                    return;
                }
                FriendManager.removeFriend(args[2]);
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Removed friend: " + EnumChatFormatting.YELLOW + args[2]));
                break;
            case "list":
                if (FriendManager.getFriends().isEmpty()) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "No friends added."));
                } else {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Friends:"));
                    for (String friend : FriendManager.getFriends()) {
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "- " + friend));
                    }
                }
                break;
            case "whitelist":
                FriendManager.setWhitelistMode(!FriendManager.isWhitelistMode());
                String wlStatus = FriendManager.isWhitelistMode() ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled";
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Whitelist mode " + wlStatus));
                break;
            case "autoaccept":
                FriendManager.setAutoAccept(!FriendManager.isAutoAccept());
                String aaStatus = FriendManager.isAutoAccept() ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled";
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Auto-accept " + aaStatus));
                break;
            default:
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Unknown option. Use: add, remove, list, whitelist, autoaccept"));
                break;
        }
    }
    private void handleGuildCommand(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] Guild Settings:"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Enabled: " + EnumChatFormatting.YELLOW + ModConfig.guildChatEnabled));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Personality: " + EnumChatFormatting.YELLOW + ModConfig.guildPersonality));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Requires Mention: " + EnumChatFormatting.YELLOW + ModConfig.guildRequiresMention));
            return;
        }
        switch (args[1].toLowerCase()) {
            case "toggle":
                ModConfig.guildChatEnabled = !ModConfig.guildChatEnabled;
                String status = ModConfig.guildChatEnabled ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled";
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Guild chat " + status));
                ModConfig.save();
                break;
            case "personality":
                if (args.length < 3) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Current guild personality: " + EnumChatFormatting.YELLOW + ModConfig.guildPersonality));
                    return;
                }
                ModConfig.guildPersonality = args[2].toLowerCase();
                ModConfig.save();
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Guild personality set to: " + EnumChatFormatting.YELLOW + args[2]));
                break;
            case "mention":
                ModConfig.guildRequiresMention = !ModConfig.guildRequiresMention;
                String mentionStatus = ModConfig.guildRequiresMention ? EnumChatFormatting.GREEN + "required" : EnumChatFormatting.RED + "not required";
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Mention in guild " + mentionStatus));
                ModConfig.save();
                break;
            default:
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat guild <toggle|personality|mention>"));
                break;
        }
    }
    private void handleVisualCommand(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] Visual Settings:"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Show Thinking: " + EnumChatFormatting.YELLOW + ModConfig.showThinking));
            return;
        }
        switch (args[1].toLowerCase()) {
            case "thinking":
                ModConfig.showThinking = !ModConfig.showThinking;
                String thinkStatus = ModConfig.showThinking ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled";
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Thinking indicator " + thinkStatus));
                ModConfig.save();
                break;
            default:
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat visual thinking"));
                break;
        }
    }
    private void handleSetCommand(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] Editable Settings:"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " maxWords " + EnumChatFormatting.GRAY + "- Max response length (" + ModConfig.maxResponseWords + ")"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " minWords " + EnumChatFormatting.GRAY + "- Min response length (" + ModConfig.minResponseWords + ")"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " contextMessages " + EnumChatFormatting.GRAY + "- Context history (" + ModConfig.maxContextMessages + ")"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " contextTimeout " + EnumChatFormatting.GRAY + "- Context timeout mins (" + ModConfig.contextTimeoutMinutes + ")"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " rememberContext " + EnumChatFormatting.GRAY + "- Context memory (" + ModConfig.rememberContext + ")"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " maxPerHour " + EnumChatFormatting.GRAY + "- Max responses/hour (" + ModConfig.maxResponsesPerHour + ")"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " aiService " + EnumChatFormatting.GRAY + "- AI service (" + ModConfig.aiService + ")"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " geminiApiKey " + EnumChatFormatting.GRAY + "- Gemini API key (*****)"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " autoTranslate " + EnumChatFormatting.GRAY + "- Auto-translate (" + ModConfig.autoTranslate + ")"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " targetLanguage " + EnumChatFormatting.GRAY + "- Target language (" + ModConfig.targetLanguage + ")"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " streaming " + EnumChatFormatting.GRAY + "- Stream responses (" + ModConfig.streamingEnabled + ")"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " badges " + EnumChatFormatting.GRAY + "- Show badges (" + ModConfig.enableBadges + ")"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Usage: /aichat set <setting> <value>"));
            return;
        }
        String setting = args[1].toLowerCase();
        switch (setting) {
            case "maxwords":
                if (args.length < 3) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat set maxwords <number>"));
                    return;
                }
                try {
                    int value = Integer.parseInt(args[2]);
                    if (value < 1 || value > 100) {
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Value must be between 1 and 100"));
                        return;
                    }
                    ModConfig.maxResponseWords = value;
                    ModConfig.save();
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Max words set to: " + EnumChatFormatting.YELLOW + value));
                } catch (NumberFormatException e) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Invalid number"));
                }
                break;
            case "minwords":
                if (args.length < 3) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat set minwords <number>"));
                    return;
                }
                try {
                    int value = Integer.parseInt(args[2]);
                    if (value < 1 || value > 50) {
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Value must be between 1 and 50"));
                        return;
                    }
                    ModConfig.minResponseWords = value;
                    ModConfig.save();
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Min words set to: " + EnumChatFormatting.YELLOW + value));
                } catch (NumberFormatException e) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Invalid number"));
                }
                break;
            case "contextmessages":
                if (args.length < 3) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat set contextmessages <number>"));
                    return;
                }
                try {
                    int value = Integer.parseInt(args[2]);
                    if (value < 0 || value > 50) {
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Value must be between 0 and 50"));
                        return;
                    }
                    ModConfig.maxContextMessages = value;
                    ModConfig.save();
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Context messages set to: " + EnumChatFormatting.YELLOW + value));
                } catch (NumberFormatException e) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Invalid number"));
                }
                break;
            case "contexttimeout":
                if (args.length < 3) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat set contexttimeout <minutes>"));
                    return;
                }
                try {
                    int value = Integer.parseInt(args[2]);
                    if (value < 1 || value > 1440) {
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Value must be between 1 and 1440 minutes"));
                        return;
                    }
                    ModConfig.contextTimeoutMinutes = value;
                    ModConfig.save();
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Context timeout set to: " + EnumChatFormatting.YELLOW + value + " minutes"));
                } catch (NumberFormatException e) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Invalid number"));
                }
                break;
            case "remembercontext":
                if (args.length < 3) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat set remembercontext <true|false>"));
                    return;
                }
                if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")) {
                    ModConfig.rememberContext = Boolean.parseBoolean(args[2]);
                    ModConfig.save();
                    String status = ModConfig.rememberContext ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled";
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Context memory " + status));
                } else {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Value must be true or false"));
                }
                break;
            case "maxperhour":
                if (args.length < 3) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat set maxperhour <number>"));
                    return;
                }
                try {
                    int value = Integer.parseInt(args[2]);
                    if (value < 1 || value > 1000) {
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Value must be between 1 and 1000"));
                        return;
                    }
                    ModConfig.maxResponsesPerHour = value;
                    ModConfig.save();
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Max responses per hour set to: " + EnumChatFormatting.YELLOW + value));
                } catch (NumberFormatException e) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Invalid number"));
                }
                break;
            case "aiservice":
                if (args.length < 3) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat set aiservice <gemini|ollama|openai|claude>"));
                    return;
                }
                String service = args[2].toLowerCase();
                if (service.equals("gemini") || service.equals("ollama") || service.equals("openai") || service.equals("claude")) {
                    ModConfig.aiService = service;
                    ModConfig.save();
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "AI service set to: " + EnumChatFormatting.YELLOW + service));
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Note: Restart required for this change to take effect"));
                } else {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Invalid service. Use: gemini, ollama, openai, or claude"));
                }
                break;
            case "geminiapikey":
                if (args.length < 3) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat set geminiApiKey <key>"));
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Get your free key at: https://aistudio.google.com/app/apikey"));
                    return;
                }
                String geminiKey = args[2];
                if (geminiKey.length() < 10) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "API key seems too short. Make sure you copied it correctly."));
                    return;
                }
                ModConfig.geminiApiKey = geminiKey;
                ModConfig.save();
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Gemini API key saved!"));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "You can now use Gemini AI for free!"));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Note: Restart required for this change to take effect"));
                break;
            case "autotranslate":
                if (args.length < 3) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat set autotranslate <true|false>"));
                    return;
                }
                if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")) {
                    ModConfig.autoTranslate = Boolean.parseBoolean(args[2]);
                    ModConfig.save();
                    String status = ModConfig.autoTranslate ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled";
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Auto-translate " + status));
                } else {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Value must be true or false"));
                }
                break;
            case "targetlanguage":
                if (args.length < 3) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat set targetlanguage <code>"));
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Examples: en (English), es (Spanish), fr (French), de (German)"));
                    return;
                }
                String lang = args[2].toLowerCase();
                if (lang.length() == 2) {
                    ModConfig.targetLanguage = lang;
                    ModConfig.save();
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Target language set to: " + EnumChatFormatting.YELLOW + lang));
                } else {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Language code must be 2 characters (ISO 639-1)"));
                }
                break;
            case "streaming":
                if (args.length < 3) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat set streaming <true|false>"));
                    return;
                }
                if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")) {
                    ModConfig.streamingEnabled = Boolean.parseBoolean(args[2]);
                    ModConfig.save();
                    String status = ModConfig.streamingEnabled ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled";
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Streaming responses " + status));
                } else {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Value must be true or false"));
                }
                break;
            case "badges":
                if (args.length < 3) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat set badges <true|false>"));
                    return;
                }
                if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")) {
                    ModConfig.enableBadges = Boolean.parseBoolean(args[2]);
                    ModConfig.save();
                    String status = ModConfig.enableBadges ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled";
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Chat badges " + status));
                } else {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Value must be true or false"));
                }
                break;
            case "emotiondetection":
                if (args.length < 3) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat set emotiondetection <true|false>"));
                    return;
                }
                if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")) {
                    ModConfig.emotionDetection = Boolean.parseBoolean(args[2]);
                    ModConfig.save();
                    String status = ModConfig.emotionDetection ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled";
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Emotion detection " + status));
                } else {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Value must be true or false"));
                }
                break;
            case "chainlimit":
                if (args.length < 3) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat set chainlimit <number>"));
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Max consecutive responses to prevent spam (1-20)"));
                    return;
                }
                try {
                    int limit = Integer.parseInt(args[2]);
                    if (limit < 1 || limit > 20) {
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Value must be between 1 and 20"));
                        return;
                    }
                    ModConfig.chainLimit = limit;
                    ModConfig.save();
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Chain limit set to: " + EnumChatFormatting.YELLOW + limit));
                } catch (NumberFormatException e) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Invalid number"));
                }
                break;
            case "learning":
                if (args.length < 3) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat set learning <true|false>"));
                    return;
                }
                if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")) {
                    ModConfig.learningEnabled = Boolean.parseBoolean(args[2]);
                    ModConfig.save();
                    String status = ModConfig.learningEnabled ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled";
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Learning system " + status));
                } else {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Value must be true or false"));
                }
                break;
            case "streamingspeed":
                if (args.length < 3) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat set streamingspeed <number>"));
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Words per second for streaming responses (1-20)"));
                    return;
                }
                try {
                    int speed = Integer.parseInt(args[2]);
                    if (speed < 1 || speed > 20) {
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Value must be between 1 and 20"));
                        return;
                    }
                    ModConfig.streamingSpeed = speed;
                    ModConfig.save();
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Streaming speed set to: " + EnumChatFormatting.YELLOW + speed + " words/sec"));
                } catch (NumberFormatException e) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Invalid number"));
                }
                break;
            case "randomdelay":
                if (args.length < 3) {
                    String current = ModConfig.randomDelay ? "enabled" : "disabled";
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Random delay is: " + EnumChatFormatting.YELLOW + current));
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Usage: /aichat set randomdelay <true|false>"));
                    return;
                }
                ModConfig.randomDelay = Boolean.parseBoolean(args[2]);
                ModConfig.save();
                String rdStatus = ModConfig.randomDelay ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled";
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Random delay " + rdStatus));
                if (ModConfig.randomDelay) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Responses will have 0-2 second random delay for natural timing"));
                }
                break;
            case "starters":
            case "conversationstarters":
                if (args.length < 3) {
                    String current = ModConfig.conversationStarters ? "enabled" : "disabled";
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Conversation starters: " + EnumChatFormatting.YELLOW + current));
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Usage: /aichat set starters <true|false>"));
                    return;
                }
                ModConfig.conversationStarters = Boolean.parseBoolean(args[2]);
                ModConfig.save();
                String csStatus = ModConfig.conversationStarters ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled";
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Conversation starters " + csStatus));
                if (ModConfig.conversationStarters) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "AI will proactively greet players and initiate conversations"));
                }
                break;
            case "confidence":
            case "showconfidence":
                if (args.length < 3) {
                    String current = ModConfig.showConfidence ? "enabled" : "disabled";
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Confidence scores: " + EnumChatFormatting.YELLOW + current));
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Usage: /aichat set confidence <true|false>"));
                    return;
                }
                ModConfig.showConfidence = Boolean.parseBoolean(args[2]);
                ModConfig.save();
                String confStatus = ModConfig.showConfidence ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled";
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Confidence scores " + confStatus));
                if (ModConfig.showConfidence) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "AI will show confidence level with each response"));
                }
                break;
            case "ratelimitwarning":
            case "rlwarning":
                if (args.length < 3) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Rate limit warning threshold: " + EnumChatFormatting.YELLOW + ModConfig.rateLimitWarningPercent + "%"));
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Usage: /aichat set ratelimitwarning <percent>"));
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Set to 0 to disable warnings"));
                    return;
                }
                try {
                    int percent = Integer.parseInt(args[2]);
                    if (percent < 0 || percent > 100) {
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Percent must be between 0 and 100"));
                        return;
                    }
                    ModConfig.rateLimitWarningPercent = percent;
                    ModConfig.save();
                    if (percent == 0) {
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Rate limit warnings " + EnumChatFormatting.RED + "disabled"));
                    } else {
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Rate limit warning threshold set to " + EnumChatFormatting.YELLOW + percent + "%"));
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "You'll be warned when reaching " + percent + "% of your hourly limit"));
                    }
                } catch (NumberFormatException e) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Invalid number. Usage: /aichat set ratelimitwarning <percent>"));
                }
                break;
                
            case "typingdelay":
            case "typing":
                if (args.length < 3) {
                    String current = ModConfig.typingDelay ? "enabled" : "disabled";
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Typing delay: " + EnumChatFormatting.YELLOW + current));
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Usage: /aichat set typingdelay <true|false>"));
                    return;
                }
                ModConfig.typingDelay = Boolean.parseBoolean(args[2]);
                ModConfig.save();
                String tdStatus = ModConfig.typingDelay ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled";
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Typing delay " + tdStatus));
                if (ModConfig.typingDelay) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Responses will simulate realistic human typing speed"));
                }
                break;
                
            default:
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Unknown setting. Use /aichat set to see available settings"));
                break;
        }
    }
    private void handleStatsCommand(ICommandSender sender, String[] args) {
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "========== AI Chat Statistics =========="));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Total Responses: " + EnumChatFormatting.YELLOW + "0"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Failed Responses: " + EnumChatFormatting.YELLOW + "0"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Success Rate: " + EnumChatFormatting.YELLOW + "100%"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Avg Response Time: " + EnumChatFormatting.YELLOW + "0ms"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Fastest Response: " + EnumChatFormatting.YELLOW + "0ms"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Slowest Response: " + EnumChatFormatting.YELLOW + "0ms"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Note: Statistics reset on restart"));
    }
    private void handleLearnCommand(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat learn <key> <value>"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Example: /aichat learn server Hypixel"));
            return;
        }
        if (args.length < 3) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat learn <key> <value>"));
            return;
        }
        String key = args[1];
        String value = joinArgs(args, 2);
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Learned: " + EnumChatFormatting.YELLOW + key + " = " + value));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "AI will remember this fact in future conversations"));
    }
    private void handleAFKCommand(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] AFK Status:"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Currently AFK: " + EnumChatFormatting.YELLOW + "false"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Auto-AFK: " + EnumChatFormatting.YELLOW + ModConfig.autoAFK));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "AFK Threshold: " + EnumChatFormatting.YELLOW + ModConfig.afkThresholdMinutes + " minutes"));
            return;
        }
        switch (args[1].toLowerCase()) {
            case "toggle":
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "AFK mode toggled"));
                break;
            case "auto":
                ModConfig.autoAFK = !ModConfig.autoAFK;
                ModConfig.save();
                String status = ModConfig.autoAFK ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled";
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Auto-AFK " + status));
                break;
            case "time":
                if (args.length < 3) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat afk time <minutes>"));
                    return;
                }
                try {
                    int minutes = Integer.parseInt(args[2]);
                    if (minutes < 1 || minutes > 60) {
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Time must be between 1 and 60 minutes"));
                        return;
                    }
                    ModConfig.afkThresholdMinutes = minutes;
                    ModConfig.save();
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "AFK threshold set to: " + EnumChatFormatting.YELLOW + minutes + " minutes"));
                } catch (NumberFormatException e) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Invalid number"));
                }
                break;
            default:
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat afk <toggle|auto|time>"));
                break;
        }
    }
    private void handleMemoryCommand(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] Memory Commands:"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " /aichat memory save " + EnumChatFormatting.GRAY + "- Manually save memory"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " /aichat memory clear [player] " + EnumChatFormatting.GRAY + "- Clear memory"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " /aichat memory stats " + EnumChatFormatting.GRAY + "- View memory statistics"));
            return;
        }
        switch (args[1].toLowerCase()) {
            case "save":
                com.aichat.context.MemoryPersistence.saveMemory();
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Memory saved to config/memory.json"));
                break;
            case "clear":
                if (args.length > 2) {
                    String player = args[2];
                    com.aichat.context.MemoryPersistence.clearMemory(player);
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Cleared memory for: " + EnumChatFormatting.YELLOW + player));
                } else {
                    com.aichat.context.MemoryPersistence.clearAllMemory();
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Cleared all memory"));
                }
                break;
            case "stats":
                java.util.Map<String, Integer> stats = com.aichat.context.MemoryPersistence.getMemoryStats();
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "========== Memory Statistics =========="));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Players in memory: " + EnumChatFormatting.YELLOW + stats.size()));
                int total = 0;
                for (int count : stats.values()) {
                    total += count;
                }
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Total messages: " + EnumChatFormatting.YELLOW + total));
                if (!stats.isEmpty()) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Top conversations:"));
                    stats.entrySet().stream()
                        .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                        .limit(5)
                        .forEach(entry -> {
                            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " - " + entry.getKey() + ": " + EnumChatFormatting.WHITE + entry.getValue() + " messages"));
                        });
                }
                break;
            default:
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat memory <save|clear|stats>"));
                break;
        }
    }
    private void handleConversationCommand(ICommandSender sender, String[] args) {
        java.util.Map<String, Long> windows = com.aichat.context.ConversationWindow.getActiveWindows();
        if (windows.isEmpty()) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.GRAY + "No active conversation windows"));
            return;
        }
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "========== Active Conversations =========="));
        for (java.util.Map.Entry<String, Long> entry : windows.entrySet()) {
            long remaining = com.aichat.context.ConversationWindow.getTimeRemaining(entry.getKey());
            long seconds = remaining / 1000;
            long minutes = seconds / 60;
            seconds = seconds % 60;
            sender.addChatMessage(new ChatComponentText(
                EnumChatFormatting.YELLOW + entry.getKey() + EnumChatFormatting.GRAY + " - " + 
                EnumChatFormatting.WHITE + minutes + "m " + seconds + "s remaining"
            ));
        }
    }
    private void handlePartyCommand(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] Party Commands:"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " /aichat party status " + EnumChatFormatting.GRAY + "- View party members"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " /aichat party invite <player> " + EnumChatFormatting.GRAY + "- Invite to party"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " /aichat party kick <player> " + EnumChatFormatting.GRAY + "- Kick from party"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " /aichat party warp " + EnumChatFormatting.GRAY + "- Warp party (leader only)"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " /aichat party leave " + EnumChatFormatting.GRAY + "- Leave party"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " /aichat party autoinvite <true|false> " + EnumChatFormatting.GRAY + "- Toggle AI auto-invite"));
            return;
        }
        switch (args[1].toLowerCase()) {
            case "status":
                if (!PartyManager.isInParty()) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.GRAY + "Not in a party"));
                } else {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "========== Party Status =========="));
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Party Leader: " + (PartyManager.isPartyLeader() ? EnumChatFormatting.GREEN + "Yes" : EnumChatFormatting.RED + "No")));
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Party Size: " + EnumChatFormatting.YELLOW + PartyManager.getPartySize()));
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Members:"));
                    for (String member : PartyManager.getPartyMembers()) {
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " - " + member));
                    }
                }
                break;
            case "invite":
                if (args.length < 3) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat party invite <player>"));
                    return;
                }
                PartyManager.invitePlayer(args[2]);
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Invited " + EnumChatFormatting.YELLOW + args[2] + EnumChatFormatting.WHITE + " to party"));
                break;
            case "kick":
                if (args.length < 3) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat party kick <player>"));
                    return;
                }
                if (!PartyManager.isPartyLeader()) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You must be party leader to kick players"));
                    return;
                }
                PartyManager.kickPlayer(args[2]);
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Kicked " + EnumChatFormatting.YELLOW + args[2] + EnumChatFormatting.WHITE + " from party"));
                break;
            case "warp":
                if (!PartyManager.isPartyLeader()) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You must be party leader to warp the party"));
                    return;
                }
                if (!ModConfig.allowWarpCommand) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Party warp is disabled. Enable with /aichat game togglewarp"));
                    return;
                }
                PartyManager.warpParty();
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Warping party to your lobby"));
                break;
            case "leave":
                PartyManager.leaveParty();
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Left party"));
                break;
            case "autoinvite":
                if (args.length < 3) {
                    String status = ModConfig.autoInviteToParty ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled";
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Auto-invite is " + status));
                    return;
                }
                ModConfig.autoInviteToParty = Boolean.parseBoolean(args[2]);
                ModConfig.save();
                String status = ModConfig.autoInviteToParty ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled";
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Auto-invite " + status));
                break;
            default:
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat party <status|invite|kick|warp|leave|autoinvite>"));
                break;
        }
    }
    private void handleGameCommand(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] Game Commands:"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " /aichat game play <mode> " + EnumChatFormatting.GRAY + "- Join a game"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " /aichat game lobby " + EnumChatFormatting.GRAY + "- Return to lobby"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " /aichat game list " + EnumChatFormatting.GRAY + "- Show available games"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " /aichat game toggleplay " + EnumChatFormatting.GRAY + "- Toggle AI /play permission"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " /aichat game togglelobby " + EnumChatFormatting.GRAY + "- Toggle AI /lobby permission"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " /aichat game togglewarp " + EnumChatFormatting.GRAY + "- Toggle AI /p warp permission"));
            return;
        }
        switch (args[1].toLowerCase()) {
            case "play":
                if (args.length < 3) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat game play <mode>"));
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Use /aichat game list to see available modes"));
                    return;
                }
                String mode = args[2].toLowerCase();
                if (!GameActionManager.isValidGameMode(mode)) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Invalid game mode. Use /aichat game list"));
                    return;
                }
                GameActionManager.playGame(mode);
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Joining " + EnumChatFormatting.YELLOW + mode));
                break;
            case "lobby":
                GameActionManager.goToLobby();
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Returning to lobby"));
                break;
            case "list":
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "========== Available Game Modes =========="));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Use /aichat game play <mode> to join:"));
                String[] games = GameActionManager.getAvailableGames();
                for (String game : games) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + " - " + game));
                }
                break;
            case "toggleplay":
                ModConfig.allowPlayCommand = !ModConfig.allowPlayCommand;
                ModConfig.save();
                String playStatus = ModConfig.allowPlayCommand ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled";
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "AI /play command " + playStatus));
                break;
            case "togglelobby":
                ModConfig.allowLobbyCommand = !ModConfig.allowLobbyCommand;
                ModConfig.save();
                String lobbyStatus = ModConfig.allowLobbyCommand ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled";
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "AI /lobby command " + lobbyStatus));
                break;
            case "togglewarp":
                ModConfig.allowWarpCommand = !ModConfig.allowWarpCommand;
                ModConfig.save();
                String warpStatus = ModConfig.allowWarpCommand ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled";
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "AI /p warp command " + warpStatus));
                break;
            default:
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /aichat game <play|lobby|list|toggleplay|togglelobby|togglewarp>"));
                break;
        }
    }
    private void handleTestAPICommand(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + EnumChatFormatting.WHITE + "Testing API connection..."));
        String service = ModConfig.aiService.toLowerCase();
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Service: " + service));
        if (service.equals("gemini")) {
            if (ModConfig.geminiApiKey.equals("your-api-key-here") || ModConfig.geminiApiKey.isEmpty()) {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "âœ— Gemini API key not configured"));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Set in config/aichat.json or use /aichat set geminiapikey <key>"));
            } else {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "âœ“ Gemini API key is set"));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Send a test message to verify connection"));
            }
        } else if (service.equals("openai")) {
            if (ModConfig.openaiApiKey.equals("your-api-key-here") || ModConfig.openaiApiKey.isEmpty()) {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "âœ— OpenAI API key not configured"));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Set in config/aichat.json"));
            } else {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "âœ“ OpenAI API key is set"));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Model: " + ModConfig.openaiModel));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Send a test message to verify connection"));
            }
        } else if (service.equals("claude")) {
            if (ModConfig.claudeApiKey.equals("your-api-key-here") || ModConfig.claudeApiKey.isEmpty()) {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "âœ— Claude API key not configured"));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Set in config/aichat.json"));
            } else {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "âœ“ Claude API key is set"));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Model: " + ModConfig.claudeModel));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Send a test message to verify connection"));
            }
        } else if (service.equals("ollama")) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "âš  Ollama runs locally - no API key needed"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Make sure Ollama is running on localhost:11434"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Send a test message to verify connection"));
        } else {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "âœ— Unknown AI service: " + service));
        }
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Current personality: " + ModConfig.personality));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Max response words: " + ModConfig.maxResponseWords));
    }
}
