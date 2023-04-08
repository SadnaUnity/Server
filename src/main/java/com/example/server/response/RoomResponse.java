package com.example.server.response;

public class RoomResponse implements Response {
    private String message;
    private Integer user_id;
    private Integer room_id;

    public RoomResponse(String message, Integer user_id, Integer room_id) {
        this.message = message;
        this.room_id = room_id;
        this.user_id = user_id;
    }
    public String getMessage() {
        return message;
    }
    public Integer getUserId() {
        return user_id;
    }
    public Integer getRoomId() {
        return room_id;
    }
}
