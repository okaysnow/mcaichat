package com.aichat.friends;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

public class FriendManager {
    
    private static Set<String> friends = new HashSet<>();
    private static Set<String> pendingRequests = new HashSet<>();
    private static boolean autoAccept = false;
    private static boolean whitelistMode = false;
    
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static File friendsFile;
    
    public static void load(File configDir) {
        friendsFile = new File(configDir, "aichat_friends.json");
        
        if (!friendsFile.exists()) {
            save();
            return;
        }
        
        try (FileReader reader = new FileReader(friendsFile)) {
            FriendData data = gson.fromJson(reader, FriendData.class);
            if (data != null) {
                friends = data.friends != null ? data.friends : new HashSet<>();
                autoAccept = data.autoAccept;
                whitelistMode = data.whitelistMode;
            }
            System.out.println("[AI Chat] Loaded " + friends.size() + " friends");
        } catch (Exception e) {
            System.err.println("[AI Chat] Error loading friends: " + e.getMessage());
        }
    }
    
    public static void save() {
        try {
            friendsFile.getParentFile().mkdirs();
            
            FriendData data = new FriendData();
            data.friends = friends;
            data.autoAccept = autoAccept;
            data.whitelistMode = whitelistMode;
            
            try (FileWriter writer = new FileWriter(friendsFile)) {
                gson.toJson(data, writer);
            }
        } catch (Exception e) {
            System.err.println("[AI Chat] Error saving friends: " + e.getMessage());
        }
    }
    
    public static void addFriend(String username) {
        friends.add(username.toLowerCase());
        save();
    }
    
    public static void removeFriend(String username) {
        friends.remove(username.toLowerCase());
        save();
    }
    
    public static boolean isFriend(String username) {
        return friends.contains(username.toLowerCase());
    }
    
    public static Set<String> getFriends() {
        return new HashSet<>(friends);
    }
    
    public static void setAutoAccept(boolean enable) {
        autoAccept = enable;
        save();
    }
    
    public static boolean isAutoAccept() {
        return autoAccept;
    }
    
    public static void setWhitelistMode(boolean enable) {
        whitelistMode = enable;
        save();
    }
    
    public static boolean isWhitelistMode() {
        return whitelistMode;
    }
    
    public static void addPendingRequest(String username) {
        pendingRequests.add(username.toLowerCase());
    }
    
    public static void removePendingRequest(String username) {
        pendingRequests.remove(username.toLowerCase());
    }
    
    public static boolean hasPendingRequest(String username) {
        return pendingRequests.contains(username.toLowerCase());
    }
    
    private static class FriendData {
        Set<String> friends;
        boolean autoAccept;
        boolean whitelistMode;
    }
}
