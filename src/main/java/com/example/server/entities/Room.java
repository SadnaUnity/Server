package com.example.server.entities;

import java.util.List;

public class Room {

    private boolean privacy;
    private int maxCapacity ;
    private int managerId;
    private int roomId;
    private List<Poster> posters;
    private String roomName;

    public Room(Boolean privacy, int managerId, int maxCapacity, int roomId, String roomName, List<Poster> posters) {
        this.maxCapacity = (maxCapacity == 0) ? 50 : maxCapacity;
        this.privacy = (privacy != null) ? privacy : false;
        this.roomId = roomId;
        this.roomName = roomName;
        this.managerId = managerId;
        this.posters = (posters != null) ? posters : null;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getRoomId() {
        return roomId;
    }

    public List<Poster> getPosters() {
        return posters;
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
