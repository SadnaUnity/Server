package com.example.server.entities;

import java.util.List;

public class JoinRoomRequest {
    //all rooms : description, photo, name, privacy-request/approved
    private Integer userId;
    private String username;
    private Integer roomId;
    private RequestStatus requestStatus;

    public JoinRoomRequest(Integer userId, Integer roomId, RequestStatus requestStatus, String username) {
        this.requestStatus = requestStatus;
        this.roomId = roomId;
        this.userId = userId;
        this.username=username;
    }

    public String getUsername() {
        return username;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public Integer getUserId() {
        return userId;
    }
    public enum RequestStatus {
        PENDING, APPROVED, DECLINED
    }
}
