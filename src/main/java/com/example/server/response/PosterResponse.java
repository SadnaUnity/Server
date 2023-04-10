package com.example.server.response;

public class PosterResponse implements Response {
    private String message;
    private Integer user_id;
    private Integer poster_id;

    public PosterResponse(String message, Integer user_id, Integer poster_id) {
        this.message = message;
        this.poster_id = poster_id;
        this.user_id = user_id;
    }
    public String getMessage() {
        return message;
    }
    public Integer getUserId() {
        return user_id;
    }

    public Integer getPosterId() {
        return poster_id;
    }
}
