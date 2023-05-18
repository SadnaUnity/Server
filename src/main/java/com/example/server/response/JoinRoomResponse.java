package com.example.server.response;

import com.example.server.entities.JoinRoomRequest;

import java.util.List;

public class JoinRoomResponse implements Response {
    private String message;
    private JoinRoomRequest joinRoomRequests;

    public JoinRoomResponse(JoinRoomRequest joinRoomRequests, String message) {
        this.joinRoomRequests=joinRoomRequests;
        this.message = message;
    }
    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JoinRoomRequest getJoinRoomRequests() {
        return joinRoomRequests;
    }
}
