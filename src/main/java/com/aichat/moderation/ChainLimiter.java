package com.aichat.moderation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class ChainLimiter {
    
    private static final Map<String, Queue<Long>> playerChains = new HashMap<>();
    private static int maxChainLength = 5;
    private static long chainResetTime = 300000;
    
    /**
     * Check if we should respond to this player based on chain limit
     * @param playerName The player to check
     * @return true if we should respond, false if chain limit reached
     */
    public static boolean canRespond(String playerName) {
        Queue<Long> chain = playerChains.computeIfAbsent(playerName, k -> new LinkedList<>());
        long now = System.currentTimeMillis();

        while (!chain.isEmpty() && now - chain.peek() > chainResetTime) {
            chain.poll();
        }

        if (chain.size() >= maxChainLength) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Record a response to a player
     * @param playerName The player we responded to
     */
    public static void recordResponse(String playerName) {
        Queue<Long> chain = playerChains.computeIfAbsent(playerName, k -> new LinkedList<>());
        chain.offer(System.currentTimeMillis());
    }
    
    /**
     * Reset chain for a specific player
     * @param playerName The player to reset
     */
    public static void resetChain(String playerName) {
        playerChains.remove(playerName);
    }
    
    /**
     * Set the maximum chain length
     * @param length Max consecutive responses
     */
    public static void setMaxChainLength(int length) {
        maxChainLength = length;
    }
    
    /**
     * Get the current chain length for a player
     * @param playerName The player to check
     * @return Number of recent responses
     */
    public static int getChainLength(String playerName) {
        Queue<Long> chain = playerChains.get(playerName);
        if (chain == null) return 0;
        
        long now = System.currentTimeMillis();

        return (int) chain.stream()
            .filter(time -> now - time <= chainResetTime)
            .count();
    }
    
    /**
     * Clear all chains
     */
    public static void clearAll() {
        playerChains.clear();
    }
    
    /**
     * Get time until chain resets for a player
     * @param playerName The player to check
     * @return Milliseconds until reset, or 0 if no chain
     */
    public static long getTimeUntilReset(String playerName) {
        Queue<Long> chain = playerChains.get(playerName);
        if (chain == null || chain.isEmpty()) return 0;
        
        long oldestTime = chain.peek();
        long elapsed = System.currentTimeMillis() - oldestTime;
        return Math.max(0, chainResetTime - elapsed);
    }
}
