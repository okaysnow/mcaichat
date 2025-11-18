package com.aichat.context;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
public class ConversationWindow {
    private static final Map<String, Long> activeWindows = new ConcurrentHashMap<>();
    private static final long WINDOW_DURATION_MS = 5 * 60 * 1000;
    public static void openWindow(String playerName) {
        activeWindows.put(playerName.toLowerCase(), System.currentTimeMillis());
        System.out.println("[AI Chat] Opened conversation window with " + playerName + " for 5 minutes");
    }
    public static boolean isWindowActive(String playerName) {
        Long windowStart = activeWindows.get(playerName.toLowerCase());
        if (windowStart == null) {
            return false;
        }
        long elapsed = System.currentTimeMillis() - windowStart;
        if (elapsed > WINDOW_DURATION_MS) {
            activeWindows.remove(playerName.toLowerCase());
            return false;
        }
        return true;
    }
    public static void extendWindow(String playerName) {
        if (isWindowActive(playerName)) {
            activeWindows.put(playerName.toLowerCase(), System.currentTimeMillis());
        }
    }
    public static void closeWindow(String playerName) {
        activeWindows.remove(playerName.toLowerCase());
        System.out.println("[AI Chat] Closed conversation window with " + playerName);
    }
    public static long getTimeRemaining(String playerName) {
        Long windowStart = activeWindows.get(playerName.toLowerCase());
        if (windowStart == null) {
            return 0;
        }
        long elapsed = System.currentTimeMillis() - windowStart;
        long remaining = WINDOW_DURATION_MS - elapsed;
        return Math.max(0, remaining);
    }
    public static Map<String, Long> getActiveWindows() {
        return new ConcurrentHashMap<>(activeWindows);
    }
    public static void clearAll() {
        activeWindows.clear();
    }
}
