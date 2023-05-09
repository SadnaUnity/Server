package com.example.server.entities;

public class AvatarPosition {
    private Avatar avatar;
    private Position position;

    public AvatarPosition(Avatar avatar, Position position) {
        this.avatar = avatar;
        this.position = position;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public Position getPosition() {
        return position;
    }
}
