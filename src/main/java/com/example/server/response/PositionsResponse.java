package com.example.server.response;

import com.example.server.entities.Avatar;
import com.example.server.entities.Position;

import java.util.Map;

public class PositionsResponse implements Response {
    private String message;
    private Map<Avatar,Position> avatarPositions;

    public PositionsResponse(Map<Avatar,Position> avatarPositions, String message) {
        this.avatarPositions = avatarPositions;
        this.message = message;
    }
    @Override
    public String getMessage() {
        return message;
    }

    public void setAvatarPositions(Map<Avatar, Position> avatarPositions) {
        this.avatarPositions = avatarPositions;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<Avatar, Position> getAvatarPositions() {
        return avatarPositions;
    }
}
