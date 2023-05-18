package com.example.server.entities;

import java.util.List;

public class RoomStatus {
    private boolean privacy;
    private int managerId;
    private int roomId;
    private String roomName;
    private String description;
//    private List<Hall> roomParticipant;
    private RoomMemberStatus roomMemberStatus;
    private JoinRoomRequest.RequestStatus requestStatus;
    public RoomStatus(Boolean privacy, int managerId, int roomId, String roomName, String description, RoomStatus.RoomMemberStatus roomMemberStatus, JoinRoomRequest.RequestStatus requestStatus) {
        this.privacy = (privacy != null) ? privacy : false;
        this.roomId = roomId;
        this.roomName = roomName;
        this.managerId = managerId;
        this.description = description;
        this.roomMemberStatus = roomMemberStatus;
        this.requestStatus=requestStatus;
//        this.roomParticipant=roomParticipant;
    }

    public JoinRoomRequest.RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(JoinRoomRequest.RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public enum RoomMemberStatus {
        MEMBER, NOT_A_MEMBER
    }

    public void setRoomMemberStatus(RoomMemberStatus roomMemberStatus) {
        this.roomMemberStatus = roomMemberStatus;
    }

    public RoomMemberStatus getRoomMemberStatus() {
        return roomMemberStatus;
    }

    public String getDescription() {
        return description;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getRoomId() {
        return roomId;
    }

    public int getManagerId() {
        return managerId;
    }

    public boolean isPrivacy() {
        return privacy;
    }

}
