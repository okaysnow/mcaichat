package com.aichat.context;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
public class TopicTracker {
    private static final Map<String, PlayerTopics> playerTopics = new ConcurrentHashMap<>();
    private static final int MAX_TOPICS = 5;
    private static final Map<String, String[]> TOPIC_KEYWORDS = new HashMap<>();
    static {
        TOPIC_KEYWORDS.put("gaming", new String[]{"game", "play", "pvp", "bedwars", "skyblock", "win", "lose", "minecraft"});
        TOPIC_KEYWORDS.put("trading", new String[]{"trade", "buy", "sell", "coins", "price", "worth", "auction"});
        TOPIC_KEYWORDS.put("help", new String[]{"help", "how", "what", "where", "when", "why", "question", "?"});
        TOPIC_KEYWORDS.put("social", new String[]{"friend", "party", "guild", "team", "join", "invite"});
        TOPIC_KEYWORDS.put("items", new String[]{"sword", "armor", "weapon", "tool", "enchant", "item", "gear"});
        TOPIC_KEYWORDS.put("building", new String[]{"build", "house", "base", "design", "place", "block"});
        TOPIC_KEYWORDS.put("grinding", new String[]{"farm", "grind", "level", "xp", "skill", "money", "coins"});
    }
    public static void analyzeAndTrack(String player, String message) {
        String lowerPlayer = player.toLowerCase();
        PlayerTopics topics = playerTopics.computeIfAbsent(lowerPlayer, k -> new PlayerTopics());
        String detectedTopic = detectTopic(message);
        if (detectedTopic != null) {
            topics.addTopic(detectedTopic);
        }
    }
    private static String detectTopic(String message) {
        String lowerMessage = message.toLowerCase();
        Map<String, Integer> topicScores = new HashMap<>();
        for (Map.Entry<String, String[]> entry : TOPIC_KEYWORDS.entrySet()) {
            String topic = entry.getKey();
            String[] keywords = entry.getValue();
            int score = 0;
            for (String keyword : keywords) {
                if (lowerMessage.contains(keyword)) {
                    score++;
                }
            }
            if (score > 0) {
                topicScores.put(topic, score);
            }
        }
        if (topicScores.isEmpty()) {
            return null;
        }
        return topicScores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }
    public static String getTopicContext(String player) {
        PlayerTopics topics = playerTopics.get(player.toLowerCase());
        if (topics == null || topics.isEmpty()) {
            return "";
        }
        StringBuilder context = new StringBuilder();
        context.append("\nRecent conversation topics with this player: ");
        List<String> recentTopics = topics.getRecentTopics();
        for (int i = 0; i < recentTopics.size(); i++) {
            context.append(recentTopics.get(i));
            if (i < recentTopics.size() - 1) {
                context.append(", ");
            }
        }
        return context.toString();
    }
    public static void clearPlayer(String player) {
        playerTopics.remove(player.toLowerCase());
    }
    public static void clearAll() {
        playerTopics.clear();
    }
    private static class PlayerTopics {
        private final Queue<String> topics = new LinkedList<>();
        private final Map<String, Integer> topicCounts = new HashMap<>();
        public void addTopic(String topic) {
            topics.offer(topic);
            topicCounts.put(topic, topicCounts.getOrDefault(topic, 0) + 1);
            while (topics.size() > MAX_TOPICS) {
                String removed = topics.poll();
                int count = topicCounts.getOrDefault(removed, 1);
                if (count <= 1) {
                    topicCounts.remove(removed);
                } else {
                    topicCounts.put(removed, count - 1);
                }
            }
        }
        public List<String> getRecentTopics() {
            return new ArrayList<>(topics);
        }
        public boolean isEmpty() {
            return topics.isEmpty();
        }
    }
}
