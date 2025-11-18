package com.aichat.hypixel;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class PartyManager {
    private static final Set<String> partyMembers = new HashSet<>();
    private static boolean isPartyLeader = false;
    private static final Pattern PARTY_JOIN_PATTERN = Pattern.compile("(?:\\[\\w+\\] )?(\\w+) joined the party\\.");
    private static final Pattern PARTY_LEAVE_PATTERN = Pattern.compile("(?:\\[\\w+\\] )?(\\w+) has left the party\\.");
    private static final Pattern PARTY_KICK_PATTERN = Pattern.compile("(?:\\[\\w+\\] )?(\\w+) has been removed from the party\\.");
    private static final Pattern PARTY_DISBAND_PATTERN = Pattern.compile("The party was disbanded");
    private static final Pattern PARTY_LEADER_PATTERN = Pattern.compile("The party was transferred to (?:\\[\\w+\\] )?(\\w+)");
    private static final Pattern YOU_LEADER_PATTERN = Pattern.compile("You have been promoted to Party Leader");
    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText();
        Matcher matcher;
        matcher = PARTY_JOIN_PATTERN.matcher(message);
        if (matcher.find()) {
            String player = matcher.group(1);
            partyMembers.add(player.toLowerCase());
            System.out.println("[AI Chat] Party member joined: " + player);
            return;
        }
        matcher = PARTY_LEAVE_PATTERN.matcher(message);
        if (matcher.find()) {
            String player = matcher.group(1);
            partyMembers.remove(player.toLowerCase());
            System.out.println("[AI Chat] Party member left: " + player);
            return;
        }
        matcher = PARTY_KICK_PATTERN.matcher(message);
        if (matcher.find()) {
            String player = matcher.group(1);
            partyMembers.remove(player.toLowerCase());
            System.out.println("[AI Chat] Party member kicked: " + player);
            return;
        }
        if (PARTY_DISBAND_PATTERN.matcher(message).find()) {
            partyMembers.clear();
            isPartyLeader = false;
            System.out.println("[AI Chat] Party disbanded");
            return;
        }
        matcher = PARTY_LEADER_PATTERN.matcher(message);
        if (matcher.find()) {
            String newLeader = matcher.group(1);
            String username = Minecraft.getMinecraft().getSession().getUsername();
            isPartyLeader = newLeader.equalsIgnoreCase(username);
            if (isPartyLeader) {
                PartyLeaderAssistant.onBecameLeader();
            }
            System.out.println("[AI Chat] New party leader: " + newLeader);
            return;
        }
        if (YOU_LEADER_PATTERN.matcher(message).find()) {
            isPartyLeader = true;
            PartyLeaderAssistant.onBecameLeader();
            System.out.println("[AI Chat] You are now party leader");
        }
    }
    public static void invitePlayer(String playerName) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/party invite " + playerName);
        });
        System.out.println("[AI Chat] Sent party invite to: " + playerName);
    }
    public static void kickPlayer(String playerName) {
        if (!isPartyLeader) {
            System.out.println("[AI Chat] Cannot kick - not party leader");
            return;
        }
        Minecraft.getMinecraft().addScheduledTask(() -> {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/party kick " + playerName);
        });
        System.out.println("[AI Chat] Kicked from party: " + playerName);
    }
    public static void warpParty() {
        if (!isPartyLeader) {
            System.out.println("[AI Chat] Cannot warp - not party leader");
            return;
        }
        Minecraft.getMinecraft().addScheduledTask(() -> {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/p warp");
        });
        System.out.println("[AI Chat] Warping party to your lobby");
    }
    public static void transferLeader(String playerName) {
        if (!isPartyLeader) {
            System.out.println("[AI Chat] Cannot transfer - not party leader");
            return;
        }
        Minecraft.getMinecraft().addScheduledTask(() -> {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/party transfer " + playerName);
        });
        isPartyLeader = false;
        System.out.println("[AI Chat] Transferred party leadership to: " + playerName);
    }
    public static void leaveParty() {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/party leave");
        });
        partyMembers.clear();
        isPartyLeader = false;
        System.out.println("[AI Chat] Left party");
    }
    public static Set<String> getPartyMembers() {
        return new HashSet<>(partyMembers);
    }
    public static boolean isInParty() {
        return !partyMembers.isEmpty() || isPartyLeader;
    }
    public static boolean isPartyLeader() {
        return isPartyLeader;
    }
    public static int getPartySize() {
        return partyMembers.size() + (isPartyLeader ? 1 : 0);
    }
    public static void setPartyLeader(boolean leader) {
        isPartyLeader = leader;
    }
}
