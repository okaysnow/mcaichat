package com.aichat;
import com.aichat.ai.AIService;
import com.aichat.ai.GeminiService;
import com.aichat.chat.ChatChannel;
import com.aichat.chat.ChatParser;
import com.aichat.config.ModConfig;
import com.aichat.context.ConversationManager;
import com.aichat.context.ConversationWindow;
import com.aichat.context.MemoryPersistence;
import com.aichat.context.TopicTracker;
import com.aichat.features.ConfidenceTracker;
import com.aichat.features.ConversationStarter;
import com.aichat.features.RateLimitMonitor;
import com.aichat.features.TypingSimulator;
import com.aichat.features.WebSearchService;
import com.aichat.features.SentimentAnalyzer;
import com.aichat.features.TeammateAnalyzer;
import com.aichat.features.SlangDictionary;
import com.aichat.features.ProfanityFilter;
import com.aichat.features.ContextAwareness;
import com.aichat.features.EventTriggers;
import com.aichat.security.ScamDetector;
import com.aichat.friends.FriendManager;
import com.aichat.hypixel.PartyLeaderAssistant;
import com.aichat.hypixel.SmartInvites;
import com.aichat.response.ResponseVariation;
import com.aichat.response.RetryLogic;
import com.aichat.spam.SpamDetector;
import com.aichat.translation.TranslationService;
import com.aichat.ui.ChatBadges;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class ChatHandler {
    public static boolean enabled = true;
    public static boolean debugMode = false;
    public static Set<String> customTriggers = new HashSet<>();
    private long lastResponseTime = 0;
    private int responsesThisHour = 0;
    private long hourStartTime = System.currentTimeMillis();
    private long lastDecayTime = System.currentTimeMillis();
    private final AIService aiService;
    public ChatHandler() {
        MemoryPersistence.loadMemory();
        this.aiService = new GeminiService();
    }
    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (!enabled) return;
        if (System.currentTimeMillis() - lastDecayTime > 60000) {
            SmartInvites.decayScores();
            lastDecayTime = System.currentTimeMillis();
        }
        String message = event.message.getUnformattedText();
        String username = ModConfig.customUsername != null ? ModConfig.customUsername : Minecraft.getMinecraft().getSession().getUsername();
        
        ChatChannel channel = ChatParser.parseMessage(message);
        if (channel == null) {
            // Filter out system notifications only if they match these patterns
            if (message.contains("joined the lobby") || 
                message.contains("left the lobby") ||
                message.contains("has quit") ||
                message.contains("reconnected") ||
                message.contains("is now AFK") ||
                message.contains("is no longer AFK") ||
                message.contains("ONLINE:") ||
                message.contains("has joined") ||
                message.contains("has disconnected")) {
                return;
            }
            
            if (debugMode) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(
                    new ChatComponentText(EnumChatFormatting.DARK_GRAY + "[DEBUG] Failed to parse channel from: " + message)
                );
            }
            return;
        }
        String sender = channel.getSender();
        String content = channel.getContent();
        ChatChannel.ChannelType channelType = channel.getType();
        if (sender != null) {
            ConversationStarter.trackPlayerActivity(sender);
        }
        if (debugMode) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(
                new ChatComponentText(EnumChatFormatting.DARK_GRAY + "[DEBUG] Parsed - Channel: " + channelType + ", Sender: " + sender)
            );
        }
        if (sender != null && sender.equalsIgnoreCase(username)) {
            if (debugMode) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(
                    new ChatComponentText(EnumChatFormatting.DARK_GRAY + "[DEBUG] Ignoring own message")
                );
            }
            return;
        }
        if (SpamDetector.isSpam(sender, content)) {
            if (debugMode) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(
                    new ChatComponentText(EnumChatFormatting.DARK_GRAY + "[DEBUG] Spam detected from " + sender)
                );
            }
            return;
        }
        if (channelType == ChatChannel.ChannelType.GUILD) {
            if (!ModConfig.guildChatEnabled) {
                if (debugMode) {
                    Minecraft.getMinecraft().thePlayer.addChatMessage(
                        new ChatComponentText(EnumChatFormatting.DARK_GRAY + "[DEBUG] Guild chat disabled")
                    );
                }
                return;
            }
            if (ModConfig.guildRequiresMention && !content.toLowerCase().contains(username.toLowerCase())) {
                if (debugMode) {
                    Minecraft.getMinecraft().thePlayer.addChatMessage(
                        new ChatComponentText(EnumChatFormatting.DARK_GRAY + "[DEBUG] Guild message requires mention")
                    );
                }
                return;
            }
        }
        if (FriendManager.isWhitelistMode() && !FriendManager.isFriend(sender)) {
            if (debugMode) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(
                    new ChatComponentText(EnumChatFormatting.DARK_GRAY + "[DEBUG] Whitelist mode: " + sender + " not in friends")
                );
            }
            return;
        }
        if (content != null && shouldRespond(content, username, sender, channelType)) {
            long currentTime = System.currentTimeMillis();
            long cooldownMillis = ModConfig.cooldownSeconds * 1000L;
            if (currentTime - lastResponseTime < cooldownMillis) {
                if (debugMode) {
                    long remaining = (cooldownMillis - (currentTime - lastResponseTime)) / 1000;
                    Minecraft.getMinecraft().thePlayer.addChatMessage(
                        new ChatComponentText(EnumChatFormatting.DARK_GRAY + "[DEBUG] Cooldown active: " + remaining + "s remaining")
                    );
                }
                return;
            }
            if (currentTime - hourStartTime > 3600000) {
                hourStartTime = currentTime;
                responsesThisHour = 0;
            }
            if (responsesThisHour >= ModConfig.maxResponsesPerHour) {
                System.out.println("[AI Chat] Rate limit reached for this hour");
                if (debugMode) {
                    Minecraft.getMinecraft().thePlayer.addChatMessage(
                        new ChatComponentText(EnumChatFormatting.DARK_GRAY + "[DEBUG] Rate limit reached: " + responsesThisHour + "/" + ModConfig.maxResponsesPerHour)
                    );
                }
                return;
            }
            SmartInvites.analyzeMessage(sender, content);
            PartyLeaderAssistant.onMemberActivity(sender);
            
            if (channelType == ChatChannel.ChannelType.PUBLIC && ModConfig.eventTriggers) {
                if (content.contains("was killed by") || content.contains("killed")) {
                    EventTriggers.triggerEvent(EventTriggers.EventType.KILL, sender);
                } else if (content.contains("died") || content.contains("was slain")) {
                    EventTriggers.triggerEvent(EventTriggers.EventType.DEATH, sender);
                } else if (content.contains("won the game") || content.contains("victory")) {
                    EventTriggers.triggerEvent(EventTriggers.EventType.WIN, sender);
                } else if (content.contains("lost the game") || content.contains("defeat")) {
                    EventTriggers.triggerEvent(EventTriggers.EventType.LOSS, sender);
                } else if (content.contains("leveled up") || content.contains("level up")) {
                    EventTriggers.triggerEvent(EventTriggers.EventType.LEVEL_UP, sender);
                } else if (content.contains("achievement") || content.contains("unlocked")) {
                    EventTriggers.triggerEvent(EventTriggers.EventType.ACHIEVEMENT, sender);
                } else if (content.contains("bed destroyed") || content.contains("bed was destroyed")) {
                    EventTriggers.triggerEvent(EventTriggers.EventType.BED_DESTROYED, sender);
                } else if (content.contains("final kill")) {
                    EventTriggers.triggerEvent(EventTriggers.EventType.FINAL_KILL, sender);
                } else if (content.contains("game starting") || content.contains("game started")) {
                    EventTriggers.triggerEvent(EventTriggers.EventType.GAME_START, sender);
                }
            }
            
            boolean isScam = false;
            if (ModConfig.scamDetection) {
                isScam = ScamDetector.detectScam(content, sender);
            }
            
            if (ModConfig.randomDelay) {
                long randomDelayMs = (long)(Math.random() * 2000);
                lastResponseTime = currentTime + randomDelayMs;
            } else {
                lastResponseTime = currentTime;
            }
            responsesThisHour++;
            RateLimitMonitor.trackResponse();
            final String finalSender = sender;
            final String finalContent = content;
            final boolean finalIsScam = isScam;
            final ChatChannel finalChannel = channel;
            if (debugMode) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(
                    new ChatComponentText(EnumChatFormatting.DARK_GRAY + "[DEBUG] Generating response for " + finalSender + " in " + channelType)
                );
            }
            if (ModConfig.showThinking && !ModConfig.silentMode) {
                String thinkingMsg = ChatBadges.formatAIMessage("... Thinking ...", true);
                Minecraft.getMinecraft().thePlayer.addChatMessage(
                    new ChatComponentText(thinkingMsg)
                );
            }
            System.out.println("[AI Chat] Message detected from " + finalSender + " in " + channelType + ": " + finalContent);
            TopicTracker.analyzeAndTrack(finalSender, finalContent);
            
            // Track question patterns for learning
            if (finalContent.contains("?") || finalContent.toLowerCase().matches(".*(how|what|why|where|when|should).*")) {
                com.aichat.features.PatternLearning.trackQuestion(finalSender, finalContent);
            }
            
            String contextMessage = finalContent;
            if (finalIsScam) {
                contextMessage = finalContent + ScamDetector.getAIWarning(finalSender);
            }
            
            if (ModConfig.sentimentAnalysis) {
                SentimentAnalyzer.SentimentResult sentiment = SentimentAnalyzer.analyze(finalContent);
                if (debugMode) {
                    Minecraft.getMinecraft().thePlayer.addChatMessage(
                        new ChatComponentText(EnumChatFormatting.DARK_GRAY + "[DEBUG] Sentiment: " + sentiment.toString())
                    );
                }
                contextMessage += SentimentAnalyzer.getPersonalityAdjustment(sentiment.type);
            }
            
            final String finalContextMessage = contextMessage;
            
            if (ModConfig.webSearch && WebSearchService.shouldSearch(finalContent)) {
                String query = WebSearchService.extractQuery(finalContent);
                if (debugMode) {
                    Minecraft.getMinecraft().thePlayer.addChatMessage(
                        new ChatComponentText(EnumChatFormatting.DARK_GRAY + "[DEBUG] Web search triggered: " + query)
                    );
                }
                WebSearchService.search(query).thenAccept(searchResults -> {
                    String messageWithSearch = finalContextMessage + "\n" + searchResults;
                    TranslationService.autoTranslate(messageWithSearch).thenAccept(translatedContent -> {
                        processAIResponse(finalSender, finalContent, translatedContent, finalChannel, channelType);
                    });
                });
            } else {
                TranslationService.autoTranslate(finalContextMessage).thenAccept(translatedContent -> {
                    processAIResponse(finalSender, finalContent, translatedContent, finalChannel, channelType);
                });
            }
        }
    }
    
    private void processAIResponse(String finalSender, String originalMessage, String translatedContent, ChatChannel finalChannel, ChatChannel.ChannelType channelType) {
                ConversationManager.addMessage(finalSender, "user", originalMessage);
                MemoryPersistence.addToMemory(finalSender, "user", originalMessage);
                String effectivePersonality = channelType == ChatChannel.ChannelType.GUILD ? 
                    ModConfig.guildPersonality : ModConfig.personality;
                String topicContext = TopicTracker.getTopicContext(finalSender);
                String variationPrompt = ResponseVariation.getVariationPrompt(finalSender);
                String mockingEnhancement = effectivePersonality.equals("mocking") ? 
                    " Be extra harsh and critical. Mock their gameplay decisions and failures. Don't hold back on the roasts." : "";
                String partyLeaderContext = PartyLeaderAssistant.getContextPrompt();
                String smartInviteContext = SmartInvites.getContextPrompt(finalSender);
                String conversationStarterContext = ConversationStarter.getContextPrompt();
                String confidencePrompt = ConfidenceTracker.getSystemPrompt();
                String partyLeaderSuggestion = PartyLeaderAssistant.checkForSuggestions();
                String inviteSuggestion = SmartInvites.checkForInviteSuggestion(finalSender);
                String conversationStarter = ConversationStarter.checkForStarter(finalSender);
                StringBuilder additionalContextBuilder = new StringBuilder();
                additionalContextBuilder.append(partyLeaderContext).append(smartInviteContext)
                    .append(conversationStarterContext).append(confidencePrompt);
                if (partyLeaderSuggestion != null) {
                    additionalContextBuilder.append("\n[SUGGESTION: ").append(partyLeaderSuggestion).append("]");
                }
                if (inviteSuggestion != null) {
                    additionalContextBuilder.append("\n[SUGGESTION: ").append(inviteSuggestion).append("]");
                }
                if (conversationStarter != null) {
                    additionalContextBuilder.append("\n[CONVERSATION STARTER: Consider using this greeting: \"").append(conversationStarter).append("\"]");
                }
                
                if (channelType == ChatChannel.ChannelType.PARTY) {
                    TeammateAnalyzer.trackPartyMember(finalSender);
                    TeammateAnalyzer.recordActivity(finalSender, TeammateAnalyzer.ActivityType.MESSAGE);
                    additionalContextBuilder.append(TeammateAnalyzer.getContextPrompt());
                    
                    if (SlangDictionary.containsSlang(originalMessage)) {
                        additionalContextBuilder.append(SlangDictionary.getContextPrompt(originalMessage));
                    }
                    
                    if (ProfanityFilter.isToxic(originalMessage)) {
                        additionalContextBuilder.append(ProfanityFilter.getContextPrompt(originalMessage));
                    }
                }
                
                ContextAwareness.detectGameMode(originalMessage);
                ContextAwareness.detectLocation(originalMessage);
                additionalContextBuilder.append(ContextAwareness.getContextPrompt());
                
                final String additionalContext = additionalContextBuilder.toString();
                
                // Add learned patterns to context
                String patternContext = com.aichat.features.PatternLearning.getLearningPrompt(finalSender);
                
                if (!aiService.isConfigured()) {
                    Minecraft.getMinecraft().thePlayer.addChatMessage(
                        new ChatComponentText(
                            ChatBadges.formatMessage(ChatBadges.BadgeType.ERROR,
                            EnumChatFormatting.RED + "Gemini API key not configured. Set in config/aichat.json")
                        )
                    );
                    return;
                }
                String myUsername = ModConfig.customUsername != null ? ModConfig.customUsername : Minecraft.getMinecraft().getSession().getUsername();
                String requestId = finalSender + "_" + System.currentTimeMillis();
                com.aichat.analytics.ResponseTimeTracker.startRequest(requestId);
                RetryLogic.retryWithBackoff(() -> 
                    aiService.generateResponse(
                        translatedContent + topicContext + variationPrompt + mockingEnhancement + additionalContext + patternContext,
                        ConversationManager.getContext(finalSender),
                        effectivePersonality,
                        ModConfig.maxResponseWords,
                        myUsername
                    ), RetryLogic.getRandomFallback()
                ).thenAccept(response -> {
                    if (response != null && !response.isEmpty()) {
                        com.aichat.analytics.ResponseTimeTracker.endRequest(requestId, finalSender, true);
                        
                        // Track successful response pattern
                        com.aichat.features.PatternLearning.trackResponse(finalSender, originalMessage, response, true);
                        
                        if (ResponseVariation.isDuplicate(finalSender, response)) {
                            System.out.println("[AI Chat] Skipping duplicate response");
                            return;
                        }
                        ResponseVariation.addResponse(finalSender, response);
                        ConversationManager.addMessage(finalSender, "assistant", response);
                        MemoryPersistence.addToMemory(finalSender, "assistant", response);
                        MemoryPersistence.saveMemory();
                        ConversationWindow.extendWindow(finalSender);
                        SmartInvites.executeAutoInvite(finalSender);
                        String confidenceResponse = ConfidenceTracker.formatResponseWithConfidence(response);
                        String hypixelSafeResponse = TypingSimulator.formatHypixelSafe(confidenceResponse);
                        String formattedResponse = finalChannel.formatResponse(hypixelSafeResponse);
                        long typingDelay = TypingSimulator.calculateTypingDelay(hypixelSafeResponse);
                        if (typingDelay > 0) {
                            try {
                                Thread.sleep(typingDelay);
                            } catch (InterruptedException e) {
                            }
                        }
                        Minecraft.getMinecraft().addScheduledTask(() -> {
                            if (Minecraft.getMinecraft().thePlayer != null) {
                                Minecraft.getMinecraft().thePlayer.sendChatMessage(formattedResponse);
                            }
                        });
                        System.out.println("[AI Chat] Responded to " + finalSender + " in " + channelType + ": " + formattedResponse);
                    }
                }).exceptionally(ex -> {
                    com.aichat.analytics.ResponseTimeTracker.endRequest(requestId, finalSender, false);
                    ex.printStackTrace();
                    System.err.println("[AI Chat] Error generating response: " + ex.getMessage());
                    return null;
                });
    }
    
    private String formatPrefix() {
        return "[AI] ";
    }
    private boolean shouldRespond(String message, String username, String sender, ChatChannel.ChannelType channelType) {
        if (ModConfig.whitelistMode && !FriendManager.isFriend(sender)) {
            return false;
        }
        
        if (channelType == ChatChannel.ChannelType.GUILD) {
            return true;
        }
        
        if (channelType == ChatChannel.ChannelType.PARTY) {
            return true;
        }
        String lowerMessage = message.toLowerCase();
        String lowerUsername = username.toLowerCase();
        if (lowerMessage.contains(lowerUsername)) {
            ConversationWindow.openWindow(sender);
            return true;
        }
        if (ConversationWindow.isWindowActive(sender)) {
            return true;
        }
        for (String trigger : customTriggers) {
            if (lowerMessage.contains(trigger)) {
                return true;
            }
        }
        return false;
    }
    private void handleGameEvent(ChatParser.GameEvent event, String username) {
        String target = event.getPrimaryPlayer();
        if (target == null || target.equalsIgnoreCase(username)) {
            return;
        }
        if (!ConversationWindow.isWindowActive(target)) {
            return;
        }
        String mockResponse = "";
        switch (event.getType()) {
            case DEATH:
                mockResponse = generateMockForDeath(target);
                break;
            case KILL:
                if (event.getSecondaryPlayer() != null && ConversationWindow.isWindowActive(event.getSecondaryPlayer())) {
                    mockResponse = generateMockForDeath(event.getSecondaryPlayer());
                    target = event.getSecondaryPlayer();
                }
                break;
            case FINAL_KILL:
                mockResponse = generateMockForDeath(target);
                break;
            case BED_DESTROY:
                break;
            case GAME_LOSS:
                break;
        }
        if (!mockResponse.isEmpty()) {
            final String finalMock = mockResponse;
            final String finalTarget = target;
            Minecraft.getMinecraft().addScheduledTask(() -> {
                if (Minecraft.getMinecraft().thePlayer != null) {
                    Minecraft.getMinecraft().thePlayer.sendChatMessage(finalMock);
                }
            });
        }
    }
    private String generateMockForDeath(String player) {
        String[] mockTemplates = {
            player + " LOL did you even try?",
            player + " that was embarrassing to watch",
            player + " maybe you should stick to a different game",
            player + " how did you even manage to die there?",
            player + " yikes, that was rough buddy",
            player + " I've seen better plays from my grandma",
            player + " did you forget how to use your keyboard?",
            player + " pro tip: don't die next time"
        };
        return mockTemplates[(int)(Math.random() * mockTemplates.length)];
    }
}
