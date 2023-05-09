package com.example.server.response;

import ch.qos.logback.core.joran.sanity.Pair;
import com.example.server.entities.Avatar;
import com.example.server.entities.AvatarPosition;
import com.example.server.entities.Position;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PositionsResponse implements Response {
    private String message;
    private List<AvatarPosition> avatarPositions;

    public PositionsResponse(List<AvatarPosition> avatarPositions, String message) {
        this.avatarPositions = avatarPositions;
        this.message = message;
    }
    @Override
    public String getMessage() {
        return message;
    }

    public void setAvatarPositions(List<AvatarPosition>  avatarPositions) {
        this.avatarPositions = avatarPositions;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<AvatarPosition>  getAvatarPositions() {
        return avatarPositions;
    }
}
