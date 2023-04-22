package com.example.server.response;

import com.example.server.entities.Poster;

public class PosterResponse implements Response {
    private String message;
    private Integer poster_id;
    private Poster poster;

    public PosterResponse(String message, Integer poster_id, Poster poster) {
        this.message = message;
        this.poster_id = poster_id;
        this.poster = poster;
    }

    public Poster getPoster() {
        return poster;
    }

    public String getMessage() {
        return message;
    }

    public Integer getPosterId() {
        return poster_id;
    }
}
