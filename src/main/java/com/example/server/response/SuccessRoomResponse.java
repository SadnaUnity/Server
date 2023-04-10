package com.example.server.response;

public class SuccessRoomResponse implements Response {
    private String message;
    private Integer user_id;
    private Integer room_id;
    private boolean privacy;
    private Integer max_capacity;

    public SuccessRoomResponse(String message, Integer user_id, Integer room_id, Integer max_capacity, boolean privacy) {
        this.message = message;
        this.room_id = room_id;
        this.user_id = user_id;
        this.max_capacity=max_capacity;
        this.privacy=privacy;
    }
    public SuccessRoomResponse(String message, Integer user_id, Integer room_id) {
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

    public boolean isPrivacy() {
        return privacy;
    }

    public Integer getMaxCapacity() {
        return max_capacity;
    }
}
