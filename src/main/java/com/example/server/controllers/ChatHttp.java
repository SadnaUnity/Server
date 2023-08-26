package com.example.server.controllers;
import com.example.server.ServerConstants;
import com.example.server.entities.ChatMessage;
import com.example.server.response.AllLastMessages;
import com.example.server.response.ChatResponse;
import com.example.server.response.Response;
import com.example.server.response.RoomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@Component
public class ChatHttp {
    private final ControllerManager controllerManager;
    private Map<Integer,List<ChatMessage>> messages;

    @Autowired
    public ChatHttp(@Lazy ControllerManager controllerManager) {
        this.controllerManager = controllerManager;
        messages = new HashMap<>();
    }

    @PutMapping("/echo")
    public ResponseEntity<Response> addMessage(@RequestBody ChatMessage message, @RequestParam Integer userId, @RequestParam Integer roomId) {
        try {
            String content = message.getContent();
            String sender = message.getSender();
            long timestamp = message.getTimestamp();
            if(!controllerManager.isUserInRoom(userId,roomId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ChatResponse(message, userId, String.format(ServerConstants.USER_NOT_A_ROOM_MEMBER, userId)));
            }
            if(!messages.containsKey(roomId)){
                List<ChatMessage> roomMessages = new ArrayList<>();
                messages.put(roomId,roomMessages);
            }
            messages.get(roomId).add(new ChatMessage(content, sender, timestamp));
            return ResponseEntity.ok().body(new ChatResponse(message,userId,"message sent successfully."));
        } catch (Exception exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ChatResponse(message, userId, ServerConstants.UNEXPECTED_ERROR));
        }
    }
    @GetMapping("/chat")
    public ResponseEntity<Response> getMessagesAfterTimestamp(@RequestParam Integer userId, @RequestParam Integer roomId, @RequestParam long timestamp) {
        if(!controllerManager.isUserInRoom(userId,roomId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ChatResponse(null, userId, String.format(ServerConstants.USER_NOT_A_ROOM_MEMBER, userId)));
        }
        List<ChatMessage> result = new ArrayList<>();
        for (ChatMessage message : messages.get(roomId)) {
            if (message.getTimestamp() > timestamp) {
                result.add(message);
            }
        }
        return ResponseEntity.ok().body(new AllLastMessages(result,"messages received successfully."));
    }
    public void deleteOldMessages() {
        long currentTime = System.currentTimeMillis();
        messages.forEach((integer, chatMessages) -> {
            Iterator<ChatMessage> iterator = chatMessages.iterator();
            while (iterator.hasNext()) {
                ChatMessage message = iterator.next();
                if (currentTime - message.getTimestamp() > 10000) { // 10 seconds in milliseconds
                    iterator.remove();
                    break;
                }
            }
        });
    }
}

