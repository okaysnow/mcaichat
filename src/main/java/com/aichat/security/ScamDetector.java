package com.aichat.security;

import com.aichat.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.regex.Pattern;

public class ScamDetector {
    
    private static final Pattern SUSPICIOUS_LINK = Pattern.compile("(?i)(bit\\.ly|tinyurl|discord\\.gg/[a-z0-9]{8,}|free-[a-z]+\\.(com|net|org)|hypixel-[a-z]+\\.(com|net|org))");
    private static final Pattern PHISHING_WORDS = Pattern.compile("(?i)(click here|free coins|free ranks|account banned|verify account|claim reward|double coins|staff impersonation|giveaway winner|you won|urgent action|suspended account|free vip|hypixel admin)");
    private static final Pattern MONEY_REQUEST = Pattern.compile("(?i)(send me|give me|trade me|lend me|borrow|loan).{0,30}(coins|money|items|rank)");
    private static final Pattern IP_STEALER = Pattern.compile("(?i)(ip logger|grabify|iplogger|link shortener)");
    private static final Pattern FAKE_STAFF = Pattern.compile("(?i)(hypixel staff|hypixel admin|hypixel mod|official hypixel|from hypixel)");
    private static final Pattern DUPLICATION = Pattern.compile("(?i)(dupe|duping|duplicate.{0,20}(coins|items)|item.{0,20}dupe|coin.{0,20}dupe)");
    private static final Pattern SOCIAL_ENGINEERING = Pattern.compile("(?i)(my.{0,10}alt|test.{0,10}trade|middle.?man|trusted.{0,10}trader|vouched)");
    
    public static boolean detectScam(String message, String sender) {
        if (message == null || message.isEmpty()) {
            return false;
        }
        
        String lowerMessage = message.toLowerCase();
        ScamType scamType = null;
        String reason = "";
        
        if (SUSPICIOUS_LINK.matcher(lowerMessage).find()) {
            scamType = ScamType.SUSPICIOUS_LINK;
            reason = "Contains suspicious shortened link";
        } else if (PHISHING_WORDS.matcher(lowerMessage).find()) {
            scamType = ScamType.PHISHING;
            reason = "Contains phishing keywords";
        } else if (MONEY_REQUEST.matcher(lowerMessage).find()) {
            scamType = ScamType.MONEY_REQUEST;
            reason = "Requesting money/items";
        } else if (IP_STEALER.matcher(lowerMessage).find()) {
            scamType = ScamType.IP_LOGGER;
            reason = "Mentions IP logging tools";
        } else if (FAKE_STAFF.matcher(lowerMessage).find()) {
            scamType = ScamType.STAFF_IMPERSONATION;
            reason = "Claims to be Hypixel staff";
        } else if (DUPLICATION.matcher(lowerMessage).find()) {
            scamType = ScamType.DUPLICATION;
            reason = "Mentions item/coin duplication";
        } else if (SOCIAL_ENGINEERING.matcher(lowerMessage).find()) {
            scamType = ScamType.SOCIAL_ENGINEERING;
            reason = "Social engineering attempt";
        }
        
        if (lowerMessage.contains("http://") || lowerMessage.contains("https://")) {
            if (!lowerMessage.contains("hypixel.net") && !lowerMessage.contains("youtube.com") && !lowerMessage.contains("twitter.com")) {
                if (scamType == null) {
                    scamType = ScamType.SUSPICIOUS_LINK;
                    reason = "Unknown external link";
                }
            }
        }
        
        if (scamType != null) {
            showScamWarning(sender, scamType, reason);
            return true;
        }
        
        return false;
    }
    
    private static void showScamWarning(String sender, ScamType type, String reason) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            String prefix = EnumChatFormatting.RED.toString() + EnumChatFormatting.BOLD + "[SCAM WARNING] " + EnumChatFormatting.RESET;
            String message = EnumChatFormatting.YELLOW + "Message from " + EnumChatFormatting.WHITE + sender + 
                           EnumChatFormatting.YELLOW + " may be a scam!";
            mc.thePlayer.addChatMessage(new ChatComponentText(prefix + message));
            
            String reasonMsg = EnumChatFormatting.GRAY + "Reason: " + EnumChatFormatting.WHITE + reason;
            mc.thePlayer.addChatMessage(new ChatComponentText("  " + reasonMsg));
            
            String typeMsg = EnumChatFormatting.GRAY + "Type: " + EnumChatFormatting.RED + type.name();
            mc.thePlayer.addChatMessage(new ChatComponentText("  " + typeMsg));
            
            String tipMsg = EnumChatFormatting.DARK_GRAY + "Tip: Never click suspicious links or share account info!";
            mc.thePlayer.addChatMessage(new ChatComponentText("  " + tipMsg));
        }
    }
    
    public static String getAIWarning(String sender) {
        return "\n[SECURITY ALERT: Message from " + sender + " contains potential scam indicators. Be cautious in your response and DO NOT encourage clicking links or sharing information. Warn the user if appropriate.]";
    }
    
    public enum ScamType {
        SUSPICIOUS_LINK,
        PHISHING,
        MONEY_REQUEST,
        IP_LOGGER,
        STAFF_IMPERSONATION,
        DUPLICATION,
        SOCIAL_ENGINEERING
    }
}
