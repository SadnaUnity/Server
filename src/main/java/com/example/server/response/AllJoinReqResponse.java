package com.example.server.response;

import com.example.server.entities.Hall;
import com.example.server.entities.JoinRoomRequest;

import java.util.List;

public class AllJoinReqResponse implements Response {
    private String message;
    private List<JoinRoomRequest> joinRoomRequests;

    public AllJoinReqResponse(List<JoinRoomRequest> joinRoomRequests, String message) {
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

    public List<JoinRoomRequest> getJoinRoomRequests() {
        return joinRoomRequests;
    }
}
