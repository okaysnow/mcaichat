package com.aichat.friends;
import com.aichat.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class FriendRequestHandler {
    private static final Pattern FRIEND_REQUEST_PATTERN = Pattern.compile("^(?:\\[\\w+\\] )?(\\w+) has sent you a friend request!");
    private static final Pattern FRIEND_REQUEST_ALT_PATTERN = Pattern.compile("^Friend request from (?:\\[\\w+\\] )?(\\w+)");
    private static boolean autoAcceptEnabled = false;
    private static boolean autoAcceptFriendsOnly = false;
    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText();
        Matcher matcher = FRIEND_REQUEST_PATTERN.matcher(message);
        if (!matcher.matches()) {
            matcher = FRIEND_REQUEST_ALT_PATTERN.matcher(message);
        }
        if (matcher.matches()) {
            String playerName = matcher.group(1);
            handleFriendRequest(playerName);
        }
    }
    private void handleFriendRequest(String playerName) {
        System.out.println("[AI Chat] Friend request detected from: " + playerName);
        if (!FriendManager.isAutoAccept()) {
            return;
        }
        if (autoAcceptFriendsOnly && !FriendManager.isFriend(playerName)) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(
                new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + 
                    EnumChatFormatting.GRAY + "Ignored friend request from " + playerName + " (not in friend list)")
            );
            return;
        }
        Minecraft.getMinecraft().addScheduledTask(() -> {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/friend accept " + playerName);
            if (!FriendManager.isFriend(playerName)) {
                FriendManager.addFriend(playerName);
            }
            Minecraft.getMinecraft().thePlayer.addChatMessage(
                new ChatComponentText(EnumChatFormatting.GOLD + "[AI Chat] " + 
                    EnumChatFormatting.GREEN + "Auto-accepted friend request from " + playerName)
            );
        });
    }
    public static void setAutoAcceptEnabled(boolean enabled) {
        autoAcceptEnabled = enabled;
    }
    public static boolean isAutoAcceptEnabled() {
        return autoAcceptEnabled;
    }
    public static void setAutoAcceptFriendsOnly(boolean enabled) {
        autoAcceptFriendsOnly = enabled;
    }
    public static boolean isAutoAcceptFriendsOnly() {
        return autoAcceptFriendsOnly;
    }
}
