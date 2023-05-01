package com.example.server.response;

import com.example.server.entities.Avatar;

public class LoginResponse implements Response {
    private Integer userId;
    private String message;
    private Avatar avatar;

    public LoginResponse(){}

    public LoginResponse(String message, Integer userId, Avatar avatar) {
        this.message = message;
        this.userId = userId;
        this.avatar = avatar;
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

    public void setUser_id(Integer userId) {
        this.userId = userId;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}