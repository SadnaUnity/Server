package com.example.server.response;

import com.example.server.entities.Room;

import java.util.Map;

public class AllRoomsResponse implements Response {
    private String message;
    private Map<Integer, String> roomsData;

    public AllRoomsResponse(Map<Integer, String> roomsData, String message) {
        this.roomsData = roomsData;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<Integer, String> getRoomsData() {
        return roomsData;
    }

    public void setRoomsData(Map<Integer, String> roomsData) {
        this.roomsData = roomsData;
    }
}
