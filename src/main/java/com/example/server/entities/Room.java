package com.example.server.entities;

public class Room {

    private boolean privacy;
    private int maxCapacity;
    private int managerId;
    private int roomId;
    private String roomName;

    public Room(boolean privacy, int managerId, int maxCapacity, int roomId, String roomName) {
        this.managerId = managerId;
        this.maxCapacity = maxCapacity;
        this.privacy = privacy;
        this.roomId = roomId;
        this.roomName = roomName;
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
