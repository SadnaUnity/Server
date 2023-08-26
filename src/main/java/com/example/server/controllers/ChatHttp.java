package com.example.server.controllers;
import com.example.server.entities.ChatMessage;
import com.example.server.response.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@Component
public class ChatHttp {
    private final ControllerManager controllerManager;
    private List<ChatMessage> messages;

    @Autowired
    public ChatHttp(@Lazy ControllerManager controllerManager) {
        this.controllerManager = controllerManager;
        messages = new ArrayList<>();
    }

    @PutMapping("/message")
    public ResponseEntity<ChatResponse> addMessage(@RequestBody ChatMessage message, @RequestParam Integer userId) {
        String content = message.getContent();
        String sender = message.getSender();
        long timestamp = message.getTimestamp();
        messages.add(new ChatMessage(content, sender, timestamp));
        return ResponseEntity.ok().body(new ChatResponse(message,userId,"message sent successfully."));
    }
    public List<ChatMessage> getMessagesAfterTimestamp(long timestamp) {
        List<ChatMessage> result = new ArrayList<>();
        for (ChatMessage message : messages) {
            if (message.getTimestamp() > timestamp) {
                result.add(message);
            }
        }
        return result;
    }
    public void deleteOldMessages() {
        long currentTime = System.currentTimeMillis();
        Iterator<ChatMessage> iterator = messages.iterator();
        while (iterator.hasNext()) {
            ChatMessage message = iterator.next();
            if (currentTime - message.getTimestamp() > 10000) { // 10 seconds in milliseconds
                iterator.remove();
            }
        }
    }
}

