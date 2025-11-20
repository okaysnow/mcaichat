package com.aichat.features;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SlangDictionary {
    
    private static final Map<Pattern, String> SLANG_TRANSLATIONS = new HashMap<>();
    
    static {
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bgg\\b", Pattern.CASE_INSENSITIVE), "good game");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bgf\\b", Pattern.CASE_INSENSITIVE), "good fight");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bgl\\b", Pattern.CASE_INSENSITIVE), "good luck");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bgl hf\\b", Pattern.CASE_INSENSITIVE), "good luck have fun");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bhf\\b", Pattern.CASE_INSENSITIVE), "have fun");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bwp\\b", Pattern.CASE_INSENSITIVE), "well played");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bns\\b", Pattern.CASE_INSENSITIVE), "nice shot");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bnt\\b", Pattern.CASE_INSENSITIVE), "nice try");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\brekt\\b", Pattern.CASE_INSENSITIVE), "destroyed");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bpwned\\b", Pattern.CASE_INSENSITIVE), "dominated");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bnoob\\b", Pattern.CASE_INSENSITIVE), "inexperienced player");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bpro\\b", Pattern.CASE_INSENSITIVE), "professional player");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bez\\b", Pattern.CASE_INSENSITIVE), "easy");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bop\\b", Pattern.CASE_INSENSITIVE), "overpowered");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bafk\\b", Pattern.CASE_INSENSITIVE), "away from keyboard");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bbrb\\b", Pattern.CASE_INSENSITIVE), "be right back");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bgtg\\b", Pattern.CASE_INSENSITIVE), "got to go");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bomw\\b", Pattern.CASE_INSENSITIVE), "on my way");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\blol\\b", Pattern.CASE_INSENSITIVE), "laughing");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\blmao\\b", Pattern.CASE_INSENSITIVE), "laughing a lot");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bwtf\\b", Pattern.CASE_INSENSITIVE), "what the heck");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bomg\\b", Pattern.CASE_INSENSITIVE), "oh my gosh");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bclutch\\b", Pattern.CASE_INSENSITIVE), "amazing last-second play");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bsweat\\b", Pattern.CASE_INSENSITIVE), "very skilled tryhard player");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\btoxic\\b", Pattern.CASE_INSENSITIVE), "rude or mean player");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bcamp\\b", Pattern.CASE_INSENSITIVE), "staying in one spot");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bspam\\b", Pattern.CASE_INSENSITIVE), "repeatedly using same attack");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bgrind\\b", Pattern.CASE_INSENSITIVE), "repetitive farming for resources");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bbuff\\b", Pattern.CASE_INSENSITIVE), "make stronger");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bnerf\\b", Pattern.CASE_INSENSITIVE), "make weaker");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bmeta\\b", Pattern.CASE_INSENSITIVE), "most effective strategy");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bgg ez\\b", Pattern.CASE_INSENSITIVE), "good game that was easy");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bl\\b(?!\\w)", Pattern.CASE_INSENSITIVE), "loss");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bw\\b(?!\\w)", Pattern.CASE_INSENSITIVE), "win");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bkek\\b", Pattern.CASE_INSENSITIVE), "laughing");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bpog\\b", Pattern.CASE_INSENSITIVE), "exciting or amazing");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bcap\\b", Pattern.CASE_INSENSITIVE), "lie or false statement");
        SLANG_TRANSLATIONS.put(Pattern.compile("\\bno cap\\b", Pattern.CASE_INSENSITIVE), "no lie, being honest");
    }
    
    public static String translateSlang(String message) {
        String translated = message;
        
        for (Map.Entry<Pattern, String> entry : SLANG_TRANSLATIONS.entrySet()) {
            translated = entry.getKey().matcher(translated).replaceAll(entry.getValue());
        }
        
        return translated;
    }
    
    public static String getContextPrompt(String originalMessage) {
        String translated = translateSlang(originalMessage);
        
        if (!translated.equals(originalMessage)) {
            return "\n[SLANG TRANSLATION: Original message contains gaming slang. Translated: \"" + translated + "\"]";
        }
        
        return "";
    }
    
    public static boolean containsSlang(String message) {
        for (Pattern pattern : SLANG_TRANSLATIONS.keySet()) {
            if (pattern.matcher(message).find()) {
                return true;
            }
        }
        return false;
    }
}
