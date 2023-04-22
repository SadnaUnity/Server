package com.example.server.entities;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Avatar {
    private Accessory accessory;
    private Color color;
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Avatar(Accessory accessory, Color color, String name) {
        this.accessory = accessory;
        this.color = color;
        this.name = name;
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
        RED,
        BLUE,
        GREEN,
        YELLOW,
        PURPLE
    }

}
