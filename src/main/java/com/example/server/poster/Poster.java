package com.example.server.poster;

public class Poster {
    private String name;
    private String imageUrl;
    private String roomName;
    private String userId;

    public Poster(String name, String imageUrl, String roomName, String userId) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.roomName = roomName;
        this.userId = userId;
    }
}