package com.aichat.analytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseTimeTracker {
    
    private static final List<Long> responseTimes = new ArrayList<>();
    private static final Map<String, Long> currentRequests = new HashMap<>();
    private static final Map<String, List<Long>> perPlayerTimes = new HashMap<>();
    
    private static int totalResponses = 0;
    private static int failedResponses = 0;
    private static long totalTokensUsed = 0;
    
    /**
     * Start timing a request
     * @param requestId Unique identifier for this request
     */
    public static void startRequest(String requestId) {
        currentRequests.put(requestId, System.currentTimeMillis());
    }
    
    /**
     * End timing a request and record the duration
     * @param requestId The request identifier
     * @param playerName The player who triggered the request
     * @param success Whether the request succeeded
     */
    public static void endRequest(String requestId, String playerName, boolean success) {
        Long startTime = currentRequests.remove(requestId);
        if (startTime == null) return;
        
        long duration = System.currentTimeMillis() - startTime;
        
        if (success) {
            responseTimes.add(duration);
            totalResponses++;

            perPlayerTimes.computeIfAbsent(playerName, k -> new ArrayList<>()).add(duration);

            if (responseTimes.size() > 100) {
                responseTimes.remove(0);
            }
        } else {
            failedResponses++;
        }
    }
    
    /**
     * Get average response time in milliseconds
     * @return Average response time
     */
    public static long getAverageResponseTime() {
        if (responseTimes.isEmpty()) return 0;
        return responseTimes.stream().mapToLong(Long::longValue).sum() / responseTimes.size();
    }
    
    /**
     * Get fastest response time
     * @return Fastest time in ms
     */
    public static long getFastestTime() {
        if (responseTimes.isEmpty()) return 0;
        return responseTimes.stream().mapToLong(Long::longValue).min().orElse(0);
    }
    
    /**
     * Get slowest response time
     * @return Slowest time in ms
     */
    public static long getSlowestTime() {
        if (responseTimes.isEmpty()) return 0;
        return responseTimes.stream().mapToLong(Long::longValue).max().orElse(0);
    }
    
    /**
     * Get total number of successful responses
     * @return Total responses
     */
    public static int getTotalResponses() {
        return totalResponses;
    }
    
    /**
     * Get total number of failed responses
     * @return Failed responses
     */
    public static int getFailedResponses() {
        return failedResponses;
    }
    
    /**
     * Get success rate as a percentage
     * @return Success rate (0-100)
     */
    public static double getSuccessRate() {
        int total = totalResponses + failedResponses;
        if (total == 0) return 0;
        return (totalResponses * 100.0) / total;
    }
    
    /**
     * Get average response time for a specific player
     * @param playerName The player to check
     * @return Average time in ms
     */
    public static long getAverageTimeForPlayer(String playerName) {
        List<Long> times = perPlayerTimes.get(playerName);
        if (times == null || times.isEmpty()) return 0;
        return times.stream().mapToLong(Long::longValue).sum() / times.size();
    }
    
    /**
     * Get total responses to a specific player
     * @param playerName The player to check
     * @return Number of responses
     */
    public static int getResponseCountForPlayer(String playerName) {
        List<Long> times = perPlayerTimes.get(playerName);
        return times == null ? 0 : times.size();
    }
    
    /**
     * Record token usage
     * @param tokens Number of tokens used
     */
    public static void recordTokens(long tokens) {
        totalTokensUsed += tokens;
    }
    
    /**
     * Get total tokens used
     * @return Total tokens
     */
    public static long getTotalTokens() {
        return totalTokensUsed;
    }
    
    /**
     * Reset all statistics
     */
    public static void reset() {
        responseTimes.clear();
        currentRequests.clear();
        perPlayerTimes.clear();
        totalResponses = 0;
        failedResponses = 0;
        totalTokensUsed = 0;
    }
    
    /**
     * Get most active player
     * @return Player name with most interactions
     */
    public static String getMostActivePlayer() {
        return perPlayerTimes.entrySet().stream()
            .max(Map.Entry.comparingByValue((a, b) -> Integer.compare(a.size(), b.size())))
            .map(Map.Entry::getKey)
            .orElse("None");
    }
}
