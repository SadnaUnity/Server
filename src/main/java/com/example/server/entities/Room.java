package com.example.server.entities;

import java.util.List;

public class Room {
    private boolean privacy;
    private int managerId;
    private int roomId;
    private List<Poster> posters;
    private String roomName;
    private String description;
    private List<Hall> roomParticipant;
    public Room(Boolean privacy, int managerId, int roomId, String roomName, List<Poster> posters, String description) {
        this.privacy = (privacy != null) ? privacy : false;
        this.roomId = roomId;
        this.roomName = roomName;
        this.managerId = managerId;
        this.posters = (posters != null) ? posters : null;
        this.description=description;
//        this.roomParticipant=roomParticipant;
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

    public List<Poster> getPosters() {
        return posters;
    }

    public int getManagerId() {
        return managerId;
    }

    public boolean isPrivacy() {
        return privacy;
    }

}
