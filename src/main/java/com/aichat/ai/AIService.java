package com.aichat.ai;
import java.util.List;
import java.util.concurrent.CompletableFuture;
public interface AIService {
    CompletableFuture<String> generateResponse(String message, List<ChatMessage> context, String personality, int maxLength, String username);
    boolean isConfigured();
    String getServiceName();
}
