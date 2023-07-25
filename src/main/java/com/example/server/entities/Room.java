package com.example.server.entities;

import java.util.List;

public class Room {
    private boolean privacy;
    private int managerId;
    private int roomId;
    private List<Poster> posters;
    private String roomName;
    private String description;
    private Background background;
    private List<Hall> roomParticipant;

    private String groupImage;

    public Room(Boolean privacy, int managerId, int roomId, String roomName, List<Poster> posters, String description, Background background, String groupImage) {
        this.privacy = (privacy != null) ? privacy : false;
        this.roomId = roomId;
        this.roomName = roomName;
        this.managerId = managerId;
        this.posters = (posters != null) ? posters : null;
        this.description=description;
        this.background = background;
        this.groupImage = groupImage;
//        this.roomParticipant=roomParticipant;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public Background getBackground() {
        return background;
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

    public enum Background {
        BACKGROUND_1, BACKGROUND_2, BACKGROUND_3, BACKGROUND_4
    }
}
