package com.example.server.entities;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Avatar {
    private Accessory accessory;
    private Color color;
    private String name;
    private Integer avatarId;

    public Avatar(Accessory accessory, Color color, String name, Integer avatarId) {
        this.accessory = (accessory != null) ? accessory : Accessory.EMPTY;
        this.color = (color != null) ? color : Color.RED;;
        this.name = name;
        this.avatarId = avatarId;
    }
    public String getName() {
        return name;
    }
    public Integer getAvatarId() {
        return avatarId;
    }
    public Accessory getAccessory() {
        return accessory;
    }
    public Color getColor() {
        return color;
    }
    public void setAvatarId(Integer avatarId) {
        this.avatarId = avatarId;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAccessory(Accessory accessory) {
        this.accessory = accessory;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    public enum Accessory {
        HEART_GLASSES,
        SANTA_HAT,
        NORMAL_GLASSES,
        COOK_HAT,
        EMPTY
    }
    public enum Color {
        RED,
        BLUE,
        GREEN,
        YELLOW,
        PURPLE,
        PINK
    }

}
