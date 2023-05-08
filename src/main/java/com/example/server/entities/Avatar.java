package com.example.server.entities;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Avatar {
    private Accessory accessory;
    private Color color;
    private Integer avatarId;

    public Avatar(){}

    public Avatar(Accessory accessory, Color color, String name, Integer avatarId) {
        this.accessory = (accessory != null) ? accessory : Accessory.EMPTY;
        this.color = (color != null) ? color : Color.RED;;
        this.avatarId = avatarId;
    }
    public Integer getAvatarId() {
        return avatarId;
    }

    // Getters and setters
    public Accessory getAccessory() {
        return accessory;
    }

    public void setAccessory(Accessory accessory) {
        this.accessory = accessory;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    // Accessory enum
    public enum Accessory {
        HEART_GLASSES,
        SANTA_HAT,
        NORMAL_GLASSES,
        COOK_HAT,
        EMPTY
    }

    // Color enum
    public enum Color {
        PINK, BLUE, GREEN, YELLOW
    }

}
