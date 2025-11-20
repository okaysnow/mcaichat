package com.aichat.features;

import java.util.HashMap;
import java.util.Map;

public class CommandShortcuts {
    
    private static final Map<String, String> SHORTCUTS = new HashMap<>();
    
    static {
        SHORTCUTS.put("q", "toggle");
        SHORTCUTS.put("m", "mute");
        SHORTCUTS.put("u", "unmute");
        SHORTCUTS.put("d", "debug");
        SHORTCUTS.put("s", "silent");
        SHORTCUTS.put("h", "help");
        SHORTCUTS.put("c", "config");
        SHORTCUTS.put("st", "stats");
        SHORTCUTS.put("t", "testapi");
        SHORTCUTS.put("rl", "ratelimitstatus");
        SHORTCUTS.put("f+", "friend add");
        SHORTCUTS.put("f-", "friend remove");
        SHORTCUTS.put("fl", "friend list");
        SHORTCUTS.put("p", "personality");
        SHORTCUTS.put("ps", "party status");
        SHORTCUTS.put("pi", "party invite");
        SHORTCUTS.put("pl", "party leave");
        SHORTCUTS.put("gp", "game play");
        SHORTCUTS.put("gl", "game lobby");
        SHORTCUTS.put("e", "emergency");
    }
    
    public static String expandShortcut(String command) {
        String lower = command.toLowerCase();
        
        if (SHORTCUTS.containsKey(lower)) {
            return SHORTCUTS.get(lower);
        }
        
        for (Map.Entry<String, String> entry : SHORTCUTS.entrySet()) {
            if (command.startsWith(entry.getKey() + " ")) {
                return entry.getValue() + command.substring(entry.getKey().length());
            }
        }
        
        return command;
    }
    
    public static boolean isShortcut(String command) {
        String lower = command.toLowerCase().split(" ")[0];
        return SHORTCUTS.containsKey(lower);
    }
    
    public static String getShortcutList() {
        StringBuilder list = new StringBuilder("Available shortcuts:\n");
        
        list.append("Quick Actions: q=toggle, m=mute, u=unmute, e=emergency\n");
        list.append("Info: h=help, c=config, st=stats, t=testapi, rl=ratelimit\n");
        list.append("Friends: f+=add, f-=remove, fl=list\n");
        list.append("Party: ps=status, pi=invite, pl=leave\n");
        list.append("Game: gp=play, gl=lobby\n");
        list.append("Other: d=debug, s=silent, p=personality\n");
        
        return list.toString();
    }
    
    public static void addCustomShortcut(String shortcut, String command) {
        SHORTCUTS.put(shortcut.toLowerCase(), command);
    }
    
    public static void removeCustomShortcut(String shortcut) {
        SHORTCUTS.remove(shortcut.toLowerCase());
    }
}
