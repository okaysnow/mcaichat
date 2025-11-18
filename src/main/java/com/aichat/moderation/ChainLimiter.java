package com.aichat.moderation;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
public class ChainLimiter {
    private static final Map<String, Queue<Long>> playerChains = new HashMap<>();
    private static int maxChainLength = 5;
    private static long chainResetTime = 300000;
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
    public static void recordResponse(String playerName) {
        Queue<Long> chain = playerChains.computeIfAbsent(playerName, k -> new LinkedList<>());
        chain.offer(System.currentTimeMillis());
    }
    public static void resetChain(String playerName) {
        playerChains.remove(playerName);
    }
    public static void setMaxChainLength(int length) {
        maxChainLength = length;
    }
    public static int getChainLength(String playerName) {
        Queue<Long> chain = playerChains.get(playerName);
        if (chain == null) return 0;
        long now = System.currentTimeMillis();
        return (int) chain.stream()
            .filter(time -> now - time <= chainResetTime)
            .count();
    }
    public static void clearAll() {
        playerChains.clear();
    }
    public static long getTimeUntilReset(String playerName) {
        Queue<Long> chain = playerChains.get(playerName);
        if (chain == null || chain.isEmpty()) return 0;
        long oldestTime = chain.peek();
        long elapsed = System.currentTimeMillis() - oldestTime;
        return Math.max(0, chainResetTime - elapsed);
    }
}
