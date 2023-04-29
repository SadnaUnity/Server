package com.example.server.response;

import com.example.server.entities.Avatar;

import java.io.Serializable;

public class AvatarResponse implements Response, Serializable {
    private Integer avatarId;
    private String message;
    private Avatar avatar;
    public AvatarResponse(String message, Integer avatarId, Avatar avatar) {
        this.message = message;
        this.avatarId = avatarId;
        this.avatar = avatar;
    }

    public Avatar getAvatar() {
        return avatar;
    }
    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public Integer getAvatarId() {
        return avatarId;
    }

    public String getMessage() {
        return message;
    }
    public Integer getUserId() {
        return avatarId;
    }

    public void setAvatarId(Integer avatarId) {
        this.avatarId = avatarId;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}