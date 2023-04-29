package com.example.server.response;

import com.example.server.entities.Avatar;

public class LoginResponse implements Response {
    private Integer user_id;
    private Integer avatar_id;
    private String message;
    private Avatar avatar;
    public LoginResponse(String message, Integer user_id, Integer avatar_id, Avatar avatar) {
        this.message = message;
        this.user_id = user_id;
        this.avatar = avatar;
        this.avatar_id=avatar_id;
    }

    public Integer getAvatar_id() {
        return avatar_id;
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