package com.aichat.chat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class ChatParser {
    private static final Pattern GUILD_PATTERN = Pattern.compile("^Guild > (?:\\[\\w+\\] )?(\\w+)(?: \\[\\w+\\])?: (.+)$");
    private static final Pattern OFFICER_PATTERN = Pattern.compile("^Officer > (?:\\[\\w+\\] )?(\\w+)(?: \\[\\w+\\])?: (.+)$");
    private static final Pattern PARTY_PATTERN = Pattern.compile("^Party > (?:\\[\\w+\\] )?(\\w+)(?: \\[\\w+\\])?: (.+)$");
    private static final Pattern WHISPER_FROM_PATTERN = Pattern.compile("^From (?:\\[\\w+\\] )?(\\w+): (.+)$");
    private static final Pattern PUBLIC_PATTERN = Pattern.compile("^(?:\\[\\w+\\] )?(\\w+)(?: \\[\\w+\\])?: (.+)$");
    private static final Pattern DEATH_PATTERN = Pattern.compile("(\\w+) (?:was|got|died|fell|drowned|burned|suffocated|slain|killed|eliminated)");
    private static final Pattern KILL_PATTERN = Pattern.compile("(\\w+) (?:killed|slain|eliminated) (\\w+)");
    private static final Pattern GAME_LOSS_PATTERN = Pattern.compile("(?:You|Your team) (?:lost|lose|defeated|eliminated)");
    private static final Pattern FINAL_KILL_PATTERN = Pattern.compile("(\\w+) was .* by (\\w+)\\. FINAL KILL!");
    private static final Pattern BED_DESTROYED_PATTERN = Pattern.compile("BED DESTRUCTION > .* bed was destroyed by (\\w+)!");
    public static ChatChannel parseMessage(String message) {
        Matcher matcher;
        matcher = GUILD_PATTERN.matcher(message);
        if (matcher.matches()) {
            return new ChatChannel(
                ChatChannel.ChannelType.GUILD,
                matcher.group(1),
                matcher.group(2),
                "/gc"
            );
        }
        matcher = OFFICER_PATTERN.matcher(message);
        if (matcher.matches()) {
            return new ChatChannel(
                ChatChannel.ChannelType.OFFICER,
                matcher.group(1),
                matcher.group(2),
                "/oc"
            );
        }
        matcher = PARTY_PATTERN.matcher(message);
        if (matcher.matches()) {
            return new ChatChannel(
                ChatChannel.ChannelType.PARTY,
                matcher.group(1),
                matcher.group(2),
                "/pc"
            );
        }
        matcher = WHISPER_FROM_PATTERN.matcher(message);
        if (matcher.matches()) {
            String sender = matcher.group(1);
            return new ChatChannel(
                ChatChannel.ChannelType.WHISPER_FROM,
                sender,
                matcher.group(2),
                "/r"
            );
        }
        matcher = PUBLIC_PATTERN.matcher(message);
        if (matcher.matches()) {
            return new ChatChannel(
                ChatChannel.ChannelType.PUBLIC,
                matcher.group(1),
                matcher.group(2),
                "/ac"
            );
        }
        return null;
    }
    public static GameEvent parseGameEvent(String message) {
        Matcher matcher;
        matcher = DEATH_PATTERN.matcher(message);
        if (matcher.find()) {
            String player = matcher.group(1);
            return new GameEvent(GameEvent.EventType.DEATH, player, message);
        }
        matcher = KILL_PATTERN.matcher(message);
        if (matcher.find()) {
            String killer = matcher.group(1);
            String victim = matcher.group(2);
            return new GameEvent(GameEvent.EventType.KILL, killer, victim, message);
        }
        matcher = FINAL_KILL_PATTERN.matcher(message);
        if (matcher.find()) {
            String killer = matcher.group(2);
            String victim = matcher.group(1);
            return new GameEvent(GameEvent.EventType.FINAL_KILL, killer, victim, message);
        }
        matcher = BED_DESTROYED_PATTERN.matcher(message);
        if (matcher.find()) {
            String player = matcher.group(1);
            return new GameEvent(GameEvent.EventType.BED_DESTROY, player, message);
        }
        matcher = GAME_LOSS_PATTERN.matcher(message);
        if (matcher.find()) {
            return new GameEvent(GameEvent.EventType.GAME_LOSS, null, message);
        }
        return null;
    }
    public static class GameEvent {
        public enum EventType {
            DEATH,
            KILL,
            FINAL_KILL,
            BED_DESTROY,
            GAME_LOSS
        }
        private final EventType type;
        private final String primaryPlayer;
        private final String secondaryPlayer;
        private final String rawMessage;
        public GameEvent(EventType type, String primaryPlayer, String rawMessage) {
            this(type, primaryPlayer, null, rawMessage);
        }
        public GameEvent(EventType type, String primaryPlayer, String secondaryPlayer, String rawMessage) {
            this.type = type;
            this.primaryPlayer = primaryPlayer;
            this.secondaryPlayer = secondaryPlayer;
            this.rawMessage = rawMessage;
        }
        public EventType getType() {
            return type;
        }
        public String getPrimaryPlayer() {
            return primaryPlayer;
        }
        public String getSecondaryPlayer() {
            return secondaryPlayer;
        }
        public String getRawMessage() {
            return rawMessage;
        }
    }
}
