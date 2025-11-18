package com.aichat.response;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
public class RetryLogic {
    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_DELAY = 1000;
    public static <T> CompletableFuture<T> retryWithBackoff(Supplier<CompletableFuture<T>> operation, String fallbackMessage) {
        return retry(operation, 0, INITIAL_DELAY, fallbackMessage);
    }
    private static <T> CompletableFuture<T> retry(Supplier<CompletableFuture<T>> operation, int attempt, long delay, String fallbackMessage) {
        return operation.get()
            .thenApply(CompletableFuture::completedFuture)
            .exceptionally(ex -> {
                if (attempt >= MAX_RETRIES - 1) {
                    System.err.println("[AI Chat] Max retries reached. Using fallback message.");
                    return CompletableFuture.completedFuture((T) fallbackMessage);
                }
                System.out.println("[AI Chat] Retry attempt " + (attempt + 1) + " after " + delay + "ms");
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return retry(operation, attempt + 1, delay * 2, fallbackMessage);
            })
            .thenCompose(f -> f);
    }
    public static String[] getFallbackMessages() {
        return new String[]{
            "Sorry, I'm having trouble connecting right now!",
            "My brain is lagging, try again in a sec!",
            "Connection issues... I'll be back!",
            "AI servers are being weird rn",
            "Technical difficulties, hold on!"
        };
    }
    public static String getRandomFallback() {
        String[] fallbacks = getFallbackMessages();
        return fallbacks[(int) (Math.random() * fallbacks.length)];
    }
}
