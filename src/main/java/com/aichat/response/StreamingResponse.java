package com.aichat.response;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class StreamingResponse {
    
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    public static CompletableFuture<Void> streamResponse(String fullResponse, String prefix, int wordsPerSecond) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        String[] words = fullResponse.split("\\s+");
        AtomicInteger index = new AtomicInteger(0);
        
        long delayMs = 1000 / wordsPerSecond;
        
        scheduler.scheduleAtFixedRate(() -> {
            try {
                int i = index.getAndIncrement();
                if (i >= words.length) {
                    future.complete(null);
                    return;
                }

                StringBuilder partial = new StringBuilder();
                for (int j = 0; j <= i; j++) {
                    partial.append(words[j]).append(" ");
                }

                Minecraft.getMinecraft().addScheduledTask(() -> {
                    Minecraft.getMinecraft().thePlayer.addChatMessage(
                        new ChatComponentText(prefix + " " + partial.toString().trim() + 
                            (i < words.length - 1 ? EnumChatFormatting.GRAY + " ..." : ""))
                    );
                });
                
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        }, 0, delayMs, TimeUnit.MILLISECONDS);
        
        return future;
    }
    
    public static void showTypingIndicator(String prefix) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(
            new ChatComponentText(prefix + " " + EnumChatFormatting.GRAY + "typing...")
        );
    }
}
