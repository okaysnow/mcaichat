package com.aichat.ui;
import com.aichat.config.ModConfig;
import net.minecraft.util.EnumChatFormatting;
public class ChatBadges {
    public enum BadgeType {
        ENABLED("âœ“", EnumChatFormatting.GREEN, "AI Enabled"),
        DISABLED("âœ—", EnumChatFormatting.RED, "AI Disabled"),
        THINKING("â‹¯", EnumChatFormatting.YELLOW, "Thinking"),
        PROCESSING(">", EnumChatFormatting.AQUA, "Processing"),
        ERROR("!", EnumChatFormatting.RED, "Error"),
        SUCCESS("+", EnumChatFormatting.GREEN, "Success"),
        RATE_LIMITED("*", EnumChatFormatting.GOLD, "Rate Limited"),
        OFFLINE("o", EnumChatFormatting.GRAY, "Offline");
        private final String symbol;
        private final EnumChatFormatting color;
        private final String tooltip;
        BadgeType(String symbol, EnumChatFormatting color, String tooltip) {
            this.symbol = symbol;
            this.color = color;
            this.tooltip = tooltip;
        }
        public String getSymbol() {
            return symbol;
        }
        public EnumChatFormatting getColor() {
            return color;
        }
        public String getTooltip() {
            return tooltip;
        }
    }
    private static boolean badgesEnabled = true;
    public static String getBadge(BadgeType type) {
        if (!badgesEnabled) {
            return "";
        }
        return type.getColor() + type.getSymbol() + EnumChatFormatting.RESET;
    }
    public static String formatBadge() {
        return "[AI] ";
    }
    public static String getStatusBadge(boolean enabled, boolean thinking, boolean error) {
        if (error) {
            return getBadge(BadgeType.ERROR);
        }
        if (thinking) {
            return getBadge(BadgeType.THINKING);
        }
        if (enabled) {
            return getBadge(BadgeType.ENABLED);
        }
        return getBadge(BadgeType.DISABLED);
    }
    public static String formatMessage(BadgeType badge, String message) {
        return getBadge(badge) + " " + message;
    }
    public static String formatAIMessage(String message, boolean isThinking) {
        BadgeType badge = isThinking ? BadgeType.THINKING : BadgeType.SUCCESS;
        return "[AI] " + getBadge(badge) + " " + EnumChatFormatting.WHITE + message;
    }
    public static void setBadgesEnabled(boolean enabled) {
        badgesEnabled = enabled;
    }
    public static boolean areBadgesEnabled() {
        return badgesEnabled;
    }
    public static String getTextBadge(BadgeType type) {
        switch (type) {
            case ENABLED: return EnumChatFormatting.GREEN + "[ON]";
            case DISABLED: return EnumChatFormatting.RED + "[OFF]";
            case THINKING: return EnumChatFormatting.YELLOW + "[...]";
            case PROCESSING: return EnumChatFormatting.AQUA + "[AI]";
            case ERROR: return EnumChatFormatting.RED + "[!]";
            case SUCCESS: return EnumChatFormatting.GREEN + "[OK]";
            case RATE_LIMITED: return EnumChatFormatting.GOLD + "[WAIT]";
            case OFFLINE: return EnumChatFormatting.GRAY + "[OFFLINE]";
            default: return "";
        }
    }
}
