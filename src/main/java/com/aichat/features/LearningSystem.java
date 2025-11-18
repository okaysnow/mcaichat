package com.aichat.features;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.*;
public class LearningSystem {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static File learningFile;
    private static final Map<String, String> learnedFacts = new HashMap<>();
    private static final Map<String, PlayerProfile> playerProfiles = new HashMap<>();
    public static class PlayerProfile {
        public String preferredName;
        public String preferredLanguage;
        public List<String> interests = new ArrayList<>();
        public List<String> dislikes = new ArrayList<>();
        public Map<String, String> customFacts = new HashMap<>();
        public int interactionCount = 0;
        public long firstSeen = System.currentTimeMillis();
        public long lastSeen = System.currentTimeMillis();
        public PlayerProfile() {}
    }
    public static void init(File configDir) {
        learningFile = new File(configDir, "learning.json");
        load();
    }
    private static void load() {
        if (!learningFile.exists()) {
            return;
        }
        try (FileReader reader = new FileReader(learningFile)) {
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> data = gson.fromJson(reader, type);
            if (data != null) {
                if (data.containsKey("facts")) {
                    Type factsType = new TypeToken<Map<String, String>>(){}.getType();
                    Map<String, String> facts = gson.fromJson(gson.toJson(data.get("facts")), factsType);
                    learnedFacts.putAll(facts);
                }
                if (data.containsKey("profiles")) {
                    Type profilesType = new TypeToken<Map<String, PlayerProfile>>(){}.getType();
                    Map<String, PlayerProfile> profiles = gson.fromJson(gson.toJson(data.get("profiles")), profilesType);
                    playerProfiles.putAll(profiles);
                }
            }
            System.out.println("[AI Chat] Loaded " + learnedFacts.size() + " facts and " + playerProfiles.size() + " player profiles");
        } catch (Exception e) {
            System.err.println("[AI Chat] Error loading learning data: " + e.getMessage());
        }
    }
    public static void save() {
        try {
            learningFile.getParentFile().mkdirs();
            Map<String, Object> data = new HashMap<>();
            data.put("facts", learnedFacts);
            data.put("profiles", playerProfiles);
            try (FileWriter writer = new FileWriter(learningFile)) {
                gson.toJson(data, writer);
            }
            System.out.println("[AI Chat] Saved learning data");
        } catch (Exception e) {
            System.err.println("[AI Chat] Error saving learning data: " + e.getMessage());
        }
    }
    public static void learnFact(String key, String value) {
        learnedFacts.put(key.toLowerCase(), value);
        save();
    }
    public static String getFact(String key) {
        return learnedFacts.get(key.toLowerCase());
    }
    public static boolean forgetFact(String key) {
        boolean removed = learnedFacts.remove(key.toLowerCase()) != null;
        if (removed) save();
        return removed;
    }
    public static Map<String, String> getAllFacts() {
        return new HashMap<>(learnedFacts);
    }
    public static PlayerProfile getProfile(String playerName) {
        return playerProfiles.computeIfAbsent(playerName, k -> new PlayerProfile());
    }
    public static void recordInteraction(String playerName) {
        PlayerProfile profile = getProfile(playerName);
        profile.interactionCount++;
        profile.lastSeen = System.currentTimeMillis();
        save();
    }
    public static void setNickname(String playerName, String nickname) {
        PlayerProfile profile = getProfile(playerName);
        profile.preferredName = nickname;
        save();
    }
    public static String getNickname(String playerName) {
        PlayerProfile profile = playerProfiles.get(playerName);
        if (profile != null && profile.preferredName != null) {
            return profile.preferredName;
        }
        return playerName;
    }
    public static void addInterest(String playerName, String interest) {
        PlayerProfile profile = getProfile(playerName);
        if (!profile.interests.contains(interest)) {
            profile.interests.add(interest);
            save();
        }
    }
    public static void addDislike(String playerName, String dislike) {
        PlayerProfile profile = getProfile(playerName);
        if (!profile.dislikes.contains(dislike)) {
            profile.dislikes.add(dislike);
            save();
        }
    }
    public static void setPlayerFact(String playerName, String key, String value) {
        PlayerProfile profile = getProfile(playerName);
        profile.customFacts.put(key, value);
        save();
    }
    public static String buildContextForPlayer(String playerName) {
        PlayerProfile profile = playerProfiles.get(playerName);
        if (profile == null) return "";
        StringBuilder context = new StringBuilder("\n\nLEARNED CONTEXT:\n");
        if (profile.preferredName != null) {
            context.append("- Prefers to be called: ").append(profile.preferredName).append("\n");
        }
        if (!profile.interests.isEmpty()) {
            context.append("- Interests: ").append(String.join(", ", profile.interests)).append("\n");
        }
        if (!profile.dislikes.isEmpty()) {
            context.append("- Dislikes: ").append(String.join(", ", profile.dislikes)).append("\n");
        }
        if (!profile.customFacts.isEmpty()) {
            context.append("- Custom facts:\n");
            for (Map.Entry<String, String> fact : profile.customFacts.entrySet()) {
                context.append("  * ").append(fact.getKey()).append(": ").append(fact.getValue()).append("\n");
            }
        }
        context.append("- You've talked ").append(profile.interactionCount).append(" times before.\n");
        return context.toString();
    }
    public static String buildGlobalContext() {
        if (learnedFacts.isEmpty()) return "";
        StringBuilder context = new StringBuilder("\n\nGLOBAL KNOWLEDGE:\n");
        for (Map.Entry<String, String> fact : learnedFacts.entrySet()) {
            context.append("- ").append(fact.getKey()).append(": ").append(fact.getValue()).append("\n");
        }
        return context.toString();
    }
    public static int getTotalInteractions() {
        return playerProfiles.values().stream()
            .mapToInt(p -> p.interactionCount)
            .sum();
    }
    public static String getMostActivePlayer() {
        return playerProfiles.entrySet().stream()
            .max(Map.Entry.comparingByValue(Comparator.comparingInt(p -> p.interactionCount)))
            .map(Map.Entry::getKey)
            .orElse("None");
    }
    public static void clearAll() {
        learnedFacts.clear();
        playerProfiles.clear();
        save();
    }
}
