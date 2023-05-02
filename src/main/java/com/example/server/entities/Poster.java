package com.example.server.entities;

public class Poster {
    private String posterName;
    private String fileUrl;
    private Integer roomId;
    private Integer userId;
    private Integer posterId;

    public Poster(String name, String fileUrl, Integer roomId, Integer userId, Integer posterId) {
        this.posterName = name;
        this.fileUrl = fileUrl;
        this.roomId = roomId;
        this.userId = userId;
        this.posterId = posterId;
    }

    public Integer getPosterId() {
        return posterId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public Integer getUserId() {
        return userId;
    }
}