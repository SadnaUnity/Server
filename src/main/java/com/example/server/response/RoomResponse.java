package com.example.server.response;

import com.example.server.entities.Room;

public class RoomResponse implements Response {
    private String message;
    private Integer roomId;
    private Room room;

    public RoomResponse(String message, Integer room_id, Room room) {
        this.message = message;
        this.roomId = room_id;
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }
    public String getMessage() {
        return message;
    }
    public Integer getRoomId() {
        return roomId;
    }

}
