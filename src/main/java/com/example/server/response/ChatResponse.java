package com.example.server.response;
import com.example.server.entities.ChatMessage;

import java.io.Serializable;

public class ChatResponse implements Response, Serializable {
    private Integer userId;
    private String message;
    private ChatMessage chatMessage;
    public ChatResponse(ChatMessage chatMessage, Integer userId, String message) {
        this.chatMessage = chatMessage;
        this.userId = userId;
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    @Override
    public String getMessage() {
        return message;
    }
}