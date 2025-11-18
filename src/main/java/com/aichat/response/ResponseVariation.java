package com.aichat.response;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
public class ResponseVariation {
    private static final Map<String, PlayerResponseHistory> playerHistories = new HashMap<>();
    private static final int MAX_HISTORY = 5;
    public static boolean isDuplicate(String player, String response) {
        PlayerResponseHistory history = playerHistories.get(player.toLowerCase());
        if (history == null) {
            return false;
        }
        return history.containsResponse(response);
    }
    public static void addResponse(String player, String response) {
        String lowerPlayer = player.toLowerCase();
        PlayerResponseHistory history = playerHistories.computeIfAbsent(lowerPlayer, k -> new PlayerResponseHistory());
        history.addResponse(response);
    }
    public static String getVariationPrompt(String player) {
        PlayerResponseHistory history = playerHistories.get(player.toLowerCase());
        if (history == null || history.isEmpty()) {
            return "";
        }
        StringBuilder prompt = new StringBuilder();
        prompt.append("\nDo not repeat these recent responses: ");
        for (String response : history.getResponses()) {
            prompt.append("\"").append(response).append("\", ");
        }
        return prompt.toString();
    }
    public static void clearPlayer(String player) {
        playerHistories.remove(player.toLowerCase());
    }
    private static class PlayerResponseHistory {
        private final Queue<String> responses = new LinkedList<>();
        public void addResponse(String response) {
            String normalized = response.toLowerCase().trim();
            responses.offer(normalized);
            while (responses.size() > MAX_HISTORY) {
                responses.poll();
            }
        }
        public boolean containsResponse(String response) {
            String normalized = response.toLowerCase().trim();
            for (String existing : responses) {
                if (existing.equals(normalized) || existing.contains(normalized) || normalized.contains(existing)) {
                    return true;
                }
            }
            return false;
        }
        public Queue<String> getResponses() {
            return new LinkedList<>(responses);
        }
        public boolean isEmpty() {
            return responses.isEmpty();
        }
    }
}
