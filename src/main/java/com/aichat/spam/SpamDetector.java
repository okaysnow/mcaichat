package com.aichat.spam;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
public class SpamDetector {
    private static final Map<String, PlayerSpamData> playerData = new HashMap<>();
    private static final int MAX_MESSAGES_PER_MINUTE = 10;
    private static final int DUPLICATE_THRESHOLD = 3;
    private static final long MESSAGE_TIMEOUT = 60000;
    public static boolean isSpam(String player, String message) {
        String lowerPlayer = player.toLowerCase();
        PlayerSpamData data = playerData.computeIfAbsent(lowerPlayer, k -> new PlayerSpamData());
        long now = System.currentTimeMillis();
        data.cleanOldMessages(now);
        if (data.getMessageCount() >= MAX_MESSAGES_PER_MINUTE) {
            System.out.println("[AI Chat] Spam detected from " + player + ": Too many messages");
            return true;
        }
        if (data.isDuplicate(message, DUPLICATE_THRESHOLD)) {
            System.out.println("[AI Chat] Spam detected from " + player + ": Repeated message");
            return true;
        }
        data.addMessage(message, now);
        return false;
    }
    public static void clearPlayer(String player) {
        playerData.remove(player.toLowerCase());
    }
    public static void clearAll() {
        playerData.clear();
    }
    private static class PlayerSpamData {
        private final Queue<MessageData> messages = new LinkedList<>();
        private final Map<String, Integer> messageCount = new HashMap<>();
        public void addMessage(String content, long timestamp) {
            messages.offer(new MessageData(content, timestamp));
            messageCount.put(content.toLowerCase(), messageCount.getOrDefault(content.toLowerCase(), 0) + 1);
        }
        public void cleanOldMessages(long currentTime) {
            while (!messages.isEmpty() && (currentTime - messages.peek().timestamp) > MESSAGE_TIMEOUT) {
                MessageData old = messages.poll();
                String key = old.content.toLowerCase();
                int count = messageCount.getOrDefault(key, 0);
                if (count <= 1) {
                    messageCount.remove(key);
                } else {
                    messageCount.put(key, count - 1);
                }
            }
        }
        public int getMessageCount() {
            return messages.size();
        }
        public boolean isDuplicate(String message, int threshold) {
            return messageCount.getOrDefault(message.toLowerCase(), 0) >= threshold;
        }
    }
    private static class MessageData {
        final String content;
        final long timestamp;
        MessageData(String content, long timestamp) {
            this.content = content;
            this.timestamp = timestamp;
        }
    }
}
