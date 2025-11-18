package com.aichat.chat;
public class ChatChannel {
    public enum ChannelType {
        PUBLIC,
        GUILD,
        PARTY,
        WHISPER_FROM,
        OFFICER,
        UNKNOWN
    }
    private final ChannelType type;
    private final String sender;
    private final String content;
    private final String commandPrefix;
    public ChatChannel(ChannelType type, String sender, String content, String commandPrefix) {
        this.type = type;
        this.sender = sender;
        this.content = content;
        this.commandPrefix = commandPrefix;
    }
    public ChannelType getType() {
        return type;
    }
    public String getSender() {
        return sender;
    }
    public String getContent() {
        return content;
    }
    public String getCommandPrefix() {
        return commandPrefix;
    }
    public String formatResponse(String response) {
        if (commandPrefix != null && !commandPrefix.isEmpty()) {
            return commandPrefix + " " + response;
        }
        return response;
    }
}
