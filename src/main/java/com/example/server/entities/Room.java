package com.example.server.entities;

public class Room {

    private boolean privacy;
    private int maxCapacity ;
    private int managerId;
    private int roomId;
    private String roomName;

    public Room(Boolean privacy, int managerId, int maxCapacity, int roomId, String roomName) {
        this.maxCapacity = (maxCapacity == 0) ? 50 : maxCapacity;
        this.privacy = (privacy != null) ? privacy : false;
        this.roomId = roomId;
        this.roomName = roomName;
        this.managerId=managerId;
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

    public int getMaxCapacity() {
        return maxCapacity;
    }
}
