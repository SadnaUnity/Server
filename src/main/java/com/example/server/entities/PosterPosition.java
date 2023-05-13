package com.example.server.entities;

public class PosterPosition {
    private Poster poster;
    private Position position;

    public PosterPosition(Poster poster, Position position) {
        this.poster = poster;
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public Poster getPoster() {
        return poster;
    }
}
