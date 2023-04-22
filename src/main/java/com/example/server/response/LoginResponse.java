package com.example.server.response;

import com.example.server.entities.Avatar;

public class LoginResponse implements Response {
    private Integer user_id;
    private String message;
    private Avatar avatar;
    public LoginResponse(String message, Integer user_id, Avatar avatar) {
        this.message = message;
        this.user_id = user_id;
        this.avatar = avatar;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public String getMessage() {
        return message;
    }

    public Integer getUserId() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}