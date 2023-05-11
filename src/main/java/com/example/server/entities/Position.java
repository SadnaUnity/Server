package com.example.server.entities;

public class Position {
    private float x;
    private float y;

    public Position(){}
    public Position(float x, float y)
    {
        this.x = x;
        this.y = y;
    }
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
