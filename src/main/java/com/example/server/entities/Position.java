package com.example.server.entities;

public class Position {
    private int id;
    private float x;
    private float y;

    public Position(){}
    public Position(int id, float x, float y)
    {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
