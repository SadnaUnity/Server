package com.example.server.response;

import com.example.server.entities.Avatar;

public class LoginResponse implements Response {
    private Integer userId;
    private String message;
    private Avatar avatar;
    private String username;

    public LoginResponse(){}

    public LoginResponse(String message, Integer user_id, String username, Avatar avatar) {
        this.message = message;
        this.userId = user_id;
        this.avatar = avatar;
        this.username = username;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public String getMessage() {
        return message;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}