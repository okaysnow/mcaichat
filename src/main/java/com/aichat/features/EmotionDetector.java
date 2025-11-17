package com.aichat.features;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class EmotionDetector {
    
    public enum Emotion {
        HAPPY("happy", "You seem cheerful! "),
        SAD("sad", "I sense you might be feeling down. "),
        ANGRY("angry", "I understand you're frustrated. "),
        EXCITED("excited", "Your enthusiasm is contagious! "),
        CONFUSED("confused", "Let me try to clarify things. "),
        GRATEFUL("grateful", "You're very welcome! "),
        WORRIED("worried", "Don't worry too much. "),
        BORED("bored", "Let's make this more interesting! "),
        NEUTRAL("neutral", "");
        
        private final String name;
        private final String responsePrefix;
        
        Emotion(String name, String responsePrefix) {
            this.name = name;
            this.responsePrefix = responsePrefix;
        }
        
        public String getResponsePrefix() {
            return responsePrefix;
        }
    }

    private static final Map<Emotion, Pattern> emotionPatterns = new HashMap<>();
    
    static {

        emotionPatterns.put(Emotion.HAPPY, Pattern.compile(
            "\\b(happy|glad|joy|awesome|great|amazing|love|perfect|excellent|wonderful|fantastic|yay|haha|lol|lmao|:D|:)|:-))\\b",
            Pattern.CASE_INSENSITIVE
        ));

        emotionPatterns.put(Emotion.SAD, Pattern.compile(
            "\\b(sad|depressed|upset|disappointed|unhappy|down|crying|cry|:sadge:|:(|:/)\\b",
            Pattern.CASE_INSENSITIVE
        ));

        emotionPatterns.put(Emotion.ANGRY, Pattern.compile(
            "\\b(angry|mad|furious|annoyed|hate|stupid|dumb|idiot|wtf|rage|pissed|>:()\\b",
            Pattern.CASE_INSENSITIVE
        ));

        emotionPatterns.put(Emotion.EXCITED, Pattern.compile(
            "\\b(excited|omg|wow|amazing|!!+|hype|cant wait|so cool|:O|:o|poggers|pog)\\b",
            Pattern.CASE_INSENSITIVE
        ));

        emotionPatterns.put(Emotion.CONFUSED, Pattern.compile(
            "\\b(confused|confusing|dont understand|what|huh|\\?\\?+|wdym|idk|not sure)\\b",
            Pattern.CASE_INSENSITIVE
        ));

        emotionPatterns.put(Emotion.GRATEFUL, Pattern.compile(
            "\\b(thanks|thank you|thx|ty|appreciate|grateful|<3)\\b",
            Pattern.CASE_INSENSITIVE
        ));

        emotionPatterns.put(Emotion.WORRIED, Pattern.compile(
            "\\b(worried|concern|afraid|scared|nervous|anxious|stress)\\b",
            Pattern.CASE_INSENSITIVE
        ));

        emotionPatterns.put(Emotion.BORED, Pattern.compile(
            "\\b(bored|boring|dull|meh|whatever|dont care|zzz)\\b",
            Pattern.CASE_INSENSITIVE
        ));
    }

    private static final Map<String, Emotion> playerEmotions = new HashMap<>();
    
    /**
     * Detect emotion from a message
     * @param message The message to analyze
     * @return Detected emotion
     */
    public static Emotion detectEmotion(String message) {
        if (message == null || message.trim().isEmpty()) {
            return Emotion.NEUTRAL;
        }

        Map<Emotion, Integer> scores = new HashMap<>();
        for (Map.Entry<Emotion, Pattern> entry : emotionPatterns.entrySet()) {
            int matches = 0;
            java.util.regex.Matcher matcher = entry.getValue().matcher(message);
            while (matcher.find()) {
                matches++;
            }
            if (matches > 0) {
                scores.put(entry.getKey(), matches);
            }
        }

        return scores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(Emotion.NEUTRAL);
    }
    
    /**
     * Update tracked emotion for a player
     * @param playerName The player
     * @param message Their message
     * @return Detected emotion
     */
    public static Emotion updatePlayerEmotion(String playerName, String message) {
        Emotion emotion = detectEmotion(message);
        playerEmotions.put(playerName, emotion);
        return emotion;
    }
    
    /**
     * Get the last known emotion for a player
     * @param playerName The player
     * @return Their last emotion
     */
    public static Emotion getPlayerEmotion(String playerName) {
        return playerEmotions.getOrDefault(playerName, Emotion.NEUTRAL);
    }
    
    /**
     * Modify AI prompt to acknowledge detected emotion
     * @param originalPrompt The original prompt
     * @param emotion Detected emotion
     * @return Modified prompt
     */
    public static String modifyPromptForEmotion(String originalPrompt, Emotion emotion) {
        if (emotion == Emotion.NEUTRAL) return originalPrompt;
        
        String emotionContext = "";
        switch (emotion) {
            case HAPPY:
                emotionContext = "The user seems happy and positive. Match their cheerful energy.";
                break;
            case SAD:
                emotionContext = "The user seems sad or disappointed. Be empathetic and supportive.";
                break;
            case ANGRY:
                emotionContext = "The user seems frustrated or angry. Stay calm and understanding.";
                break;
            case EXCITED:
                emotionContext = "The user is very excited! Share their enthusiasm.";
                break;
            case CONFUSED:
                emotionContext = "The user seems confused. Be extra clear and helpful in your explanation.";
                break;
            case GRATEFUL:
                emotionContext = "The user is expressing gratitude. Be gracious and friendly.";
                break;
            case WORRIED:
                emotionContext = "The user seems worried or anxious. Be reassuring and helpful.";
                break;
            case BORED:
                emotionContext = "The user seems bored. Try to make things more engaging.";
                break;
        }
        
        return originalPrompt + "\n\nEMOTIONAL CONTEXT: " + emotionContext;
    }
    
    /**
     * Get emotion intensity (0-10)
     * @param message The message to analyze
     * @return Intensity score
     */
    public static int getEmotionIntensity(String message) {
        if (message == null) return 0;
        
        int intensity = 0;

        intensity += (int) message.chars().filter(ch -> ch == '!').count();

        intensity += (int) message.chars().filter(ch -> ch == '?').count() / 2;

        String[] words = message.split("\\s+");
        for (String word : words) {
            if (word.length() > 2 && word.equals(word.toUpperCase())) {
                intensity += 2;
            }
        }

        if (message.contains(":)") || message.contains(":(") || message.contains(":D")) {
            intensity++;
        }
        
        return Math.min(10, intensity);
    }
    
    /**
     * Clear emotion history for a player
     * @param playerName The player
     */
    public static void clearPlayerEmotion(String playerName) {
        playerEmotions.remove(playerName);
    }
    
    /**
     * Clear all emotion history
     */
    public static void clearAll() {
        playerEmotions.clear();
    }
}
