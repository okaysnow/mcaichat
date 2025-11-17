package com.aichat.ai;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AIService {
    
    /**
     * Generate a response from the AI based on the message and conversation history
     * @param message The current message to respond to
     * @param context Previous conversation context
     * @param personality The personality/tone to use
     * @param maxLength Maximum response length in words
     * @return CompletableFuture with the AI response
     */
    CompletableFuture<String> generateResponse(String message, List<ChatMessage> context, String personality, int maxLength);
    
    /**
     * Check if the service is configured and ready
     * @return true if API key is set and service is ready
     */
    boolean isConfigured();
    
    /**
     * Get the service name (OpenAI, Claude, etc.)
     * @return Service identifier
     */
    String getServiceName();
}
