package com.example.server.response;

import com.example.server.entities.RoomStatus;

import java.util.List;
import java.util.Map;

public class HallResponse implements Response {
    private String message;
    private List<RoomStatus> roomStatuses;

    public HallResponse(List<RoomStatus> roomStatuses, String message) {
        this.roomStatuses=roomStatuses;
        this.message = message;
    }
    @Override
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public List<RoomStatus> getRoomStatuses() {
        return roomStatuses;
    }
    public void setRoomStatuses(List<RoomStatus> roomStatuses) {
        this.roomStatuses = roomStatuses;
    }
}
