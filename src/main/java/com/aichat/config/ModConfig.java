package com.aichat.config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
public class ModConfig {
    public static String geminiApiKey = "your-api-key-here";
    public static String personality = "friendly";
    public static int maxResponseWords = 20;
    public static int minResponseWords = 3;
    public static int maxContextMessages = 10;
    public static boolean rememberContext = true;
    public static int contextTimeoutMinutes = 30;
    public static boolean autoTranslate = false;
    public static String targetLanguage = "en";
    public static boolean detectLanguage = true;
    public static int cooldownSeconds = 3;
    public static int maxResponsesPerHour = 50;
    public static boolean guildChatEnabled = true;
    public static String guildPersonality = "friendly";
    public static boolean guildRequiresMention = false;
    public static boolean showThinking = true;
    public static boolean enableBadges = true;
    public static boolean streamingEnabled = false;
    public static int streamingSpeed = 5;
    public static int chainLimit = 5;
    public static int chainResetMinutes = 5;
    public static int afkThresholdMinutes = 5;
    public static boolean autoAFK = true;
    public static boolean emotionDetection = true;
    public static boolean learningEnabled = true;
    public static boolean allowPlayCommand = false;
    public static boolean allowLobbyCommand = false;
    public static boolean allowWarpCommand = false;
    public static boolean autoInviteToParty = false;
    public static boolean silentMode = false;
    public static boolean randomDelay = false;
    public static boolean typingDelay = true;
    public static boolean scamDetection = true;
    public static boolean conversationStarters = false;
    public static boolean showConfidence = false;
    public static int rateLimitWarningPercent = 80;
    public static boolean webSearch = true;
    public static boolean sentimentAnalysis = true;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static File configFile;
    public static void load(File configDir) {
        configFile = new File(configDir, "aichat.json");
        if (!configFile.exists()) {
            save();
            return;
        }
        try (FileReader reader = new FileReader(configFile)) {
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            if (json.has("geminiApiKey")) geminiApiKey = json.get("geminiApiKey").getAsString();
            if (json.has("personality")) personality = json.get("personality").getAsString();
            if (json.has("maxResponseWords")) maxResponseWords = json.get("maxResponseWords").getAsInt();
            if (json.has("minResponseWords")) minResponseWords = json.get("minResponseWords").getAsInt();
            if (json.has("maxContextMessages")) maxContextMessages = json.get("maxContextMessages").getAsInt();
            if (json.has("rememberContext")) rememberContext = json.get("rememberContext").getAsBoolean();
            if (json.has("contextTimeoutMinutes")) contextTimeoutMinutes = json.get("contextTimeoutMinutes").getAsInt();
            if (json.has("autoTranslate")) autoTranslate = json.get("autoTranslate").getAsBoolean();
            if (json.has("targetLanguage")) targetLanguage = json.get("targetLanguage").getAsString();
            if (json.has("detectLanguage")) detectLanguage = json.get("detectLanguage").getAsBoolean();
            if (json.has("cooldownSeconds")) cooldownSeconds = json.get("cooldownSeconds").getAsInt();
            if (json.has("maxResponsesPerHour")) maxResponsesPerHour = json.get("maxResponsesPerHour").getAsInt();
            if (json.has("guildChatEnabled")) guildChatEnabled = json.get("guildChatEnabled").getAsBoolean();
            if (json.has("guildPersonality")) guildPersonality = json.get("guildPersonality").getAsString();
            if (json.has("guildRequiresMention")) guildRequiresMention = json.get("guildRequiresMention").getAsBoolean();
            if (json.has("showThinking")) showThinking = json.get("showThinking").getAsBoolean();
            if (json.has("enableBadges")) enableBadges = json.get("enableBadges").getAsBoolean();
            if (json.has("streamingEnabled")) streamingEnabled = json.get("streamingEnabled").getAsBoolean();
            if (json.has("streamingSpeed")) streamingSpeed = json.get("streamingSpeed").getAsInt();
            if (json.has("chainLimit")) chainLimit = json.get("chainLimit").getAsInt();
            if (json.has("chainResetMinutes")) chainResetMinutes = json.get("chainResetMinutes").getAsInt();
            if (json.has("afkThresholdMinutes")) afkThresholdMinutes = json.get("afkThresholdMinutes").getAsInt();
            if (json.has("autoAFK")) autoAFK = json.get("autoAFK").getAsBoolean();
            if (json.has("emotionDetection")) emotionDetection = json.get("emotionDetection").getAsBoolean();
            if (json.has("learningEnabled")) learningEnabled = json.get("learningEnabled").getAsBoolean();
            if (json.has("allowPlayCommand")) allowPlayCommand = json.get("allowPlayCommand").getAsBoolean();
            if (json.has("allowLobbyCommand")) allowLobbyCommand = json.get("allowLobbyCommand").getAsBoolean();
            if (json.has("allowWarpCommand")) allowWarpCommand = json.get("allowWarpCommand").getAsBoolean();
            if (json.has("autoInviteToParty")) autoInviteToParty = json.get("autoInviteToParty").getAsBoolean();
            if (json.has("silentMode")) silentMode = json.get("silentMode").getAsBoolean();
            if (json.has("randomDelay")) randomDelay = json.get("randomDelay").getAsBoolean();
            if (json.has("typingDelay")) typingDelay = json.get("typingDelay").getAsBoolean();
            if (json.has("scamDetection")) scamDetection = json.get("scamDetection").getAsBoolean();
            if (json.has("conversationStarters")) conversationStarters = json.get("conversationStarters").getAsBoolean();
            if (json.has("showConfidence")) showConfidence = json.get("showConfidence").getAsBoolean();
            if (json.has("rateLimitWarningPercent")) rateLimitWarningPercent = json.get("rateLimitWarningPercent").getAsInt();
            if (json.has("webSearch")) webSearch = json.get("webSearch").getAsBoolean();
            if (json.has("sentimentAnalysis")) sentimentAnalysis = json.get("sentimentAnalysis").getAsBoolean();
            System.out.println("[AI Chat] Configuration loaded");
        } catch (Exception e) {
            System.err.println("[AI Chat] Error loading config: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static void save() {
        try {
            configFile.getParentFile().mkdirs();
            JsonObject json = new JsonObject();
            json.addProperty("geminiApiKey", geminiApiKey);
            json.addProperty("personality", personality);
            json.addProperty("maxResponseWords", maxResponseWords);
            json.addProperty("minResponseWords", minResponseWords);
            json.addProperty("maxContextMessages", maxContextMessages);
            json.addProperty("rememberContext", rememberContext);
            json.addProperty("contextTimeoutMinutes", contextTimeoutMinutes);
            json.addProperty("autoTranslate", autoTranslate);
            json.addProperty("targetLanguage", targetLanguage);
            json.addProperty("detectLanguage", detectLanguage);
            json.addProperty("cooldownSeconds", cooldownSeconds);
            json.addProperty("maxResponsesPerHour", maxResponsesPerHour);
            json.addProperty("guildChatEnabled", guildChatEnabled);
            json.addProperty("guildPersonality", guildPersonality);
            json.addProperty("guildRequiresMention", guildRequiresMention);
            json.addProperty("showThinking", showThinking);
            json.addProperty("enableBadges", enableBadges);
            json.addProperty("streamingEnabled", streamingEnabled);
            json.addProperty("streamingSpeed", streamingSpeed);
            json.addProperty("chainLimit", chainLimit);
            json.addProperty("chainResetMinutes", chainResetMinutes);
            json.addProperty("afkThresholdMinutes", afkThresholdMinutes);
            json.addProperty("autoAFK", autoAFK);
            json.addProperty("emotionDetection", emotionDetection);
            json.addProperty("learningEnabled", learningEnabled);
            json.addProperty("allowPlayCommand", allowPlayCommand);
            json.addProperty("allowLobbyCommand", allowLobbyCommand);
            json.addProperty("allowWarpCommand", allowWarpCommand);
            json.addProperty("autoInviteToParty", autoInviteToParty);
            json.addProperty("silentMode", silentMode);
            json.addProperty("randomDelay", randomDelay);
            json.addProperty("typingDelay", typingDelay);
            json.addProperty("scamDetection", scamDetection);
            json.addProperty("conversationStarters", conversationStarters);
            json.addProperty("showConfidence", showConfidence);
            json.addProperty("rateLimitWarningPercent", rateLimitWarningPercent);
            json.addProperty("webSearch", webSearch);
            json.addProperty("sentimentAnalysis", sentimentAnalysis);
            try (FileWriter writer = new FileWriter(configFile)) {
                gson.toJson(json, writer);
            }
            System.out.println("[AI Chat] Configuration saved to " + configFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("[AI Chat] Error saving config: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
