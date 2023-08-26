package com.example.server.chat;
import com.example.server.controllers.ChatController;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledFunctionRunner {
    static ChatController chatController;

    public static void main(String[] args) {
        // Create a ScheduledExecutorService with a single thread
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        // Schedule the function to run every 15 seconds
        executor.scheduleAtFixedRate(ScheduledFunctionRunner::yourFunction, 0, 15, TimeUnit.SECONDS);

        // Keep the main thread alive to allow scheduled tasks to run
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Shutdown the executor when no longer needed
        executor.shutdown();
    }
    private static void yourFunction() {
        chatController.deleteOldMessages();
    }

}
