package com.example.server.response;

import com.example.server.entities.Hall;
import com.example.server.entities.JoinRoomRequest;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class AllJoinReqResponse implements Response {
    private String message;
    private List<JoinRoomRequest> joinRoomRequests;
    @JsonInclude(JsonInclude.Include.NON_NULL) // Exclude field if null
    private List<JoinRoomRequest> notHandledJoinRoomRequests;

    public AllJoinReqResponse(List<JoinRoomRequest> joinRoomRequests, String message) {
        this.joinRoomRequests=joinRoomRequests;
        this.message = message;
    }
    public AllJoinReqResponse(List<JoinRoomRequest> joinRoomRequests, String message, List<JoinRoomRequest> notHandledJoinRoomRequests ) {
        this.joinRoomRequests = joinRoomRequests;
        this.message = message;
        this.notHandledJoinRoomRequests = notHandledJoinRoomRequests;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<JoinRoomRequest> getNotHandledJoinRoomRequests() {
        return notHandledJoinRoomRequests;
    }

    public List<JoinRoomRequest> getJoinRoomRequests() {
        return joinRoomRequests;
    }
}
