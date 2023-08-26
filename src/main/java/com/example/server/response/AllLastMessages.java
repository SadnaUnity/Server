package com.example.server.response;

import com.example.server.entities.ChatMessage;
import com.example.server.entities.JoinRoomRequest;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class AllLastMessages implements Response {
    private String message;
    private List<ChatMessage> messageList;
    public AllLastMessages(List<ChatMessage> messageList, String message) {
        this.messageList=messageList;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public List<ChatMessage> getMessageList() {
        return messageList;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessageList(List<ChatMessage> messageList) {
        this.messageList = messageList;
    }
}
