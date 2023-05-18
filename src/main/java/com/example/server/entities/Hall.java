package com.example.server.entities;

import java.util.List;

public class Hall {
    //all rooms : description, photo, name, privacy-request/approved
    private List<RoomStatus> roomStatuses;

    public Hall(List<RoomStatus> roomStatus){
        this.roomStatuses=roomStatus;
    }

    public List<RoomStatus> getRoomStatuses() {
        return roomStatuses;
    }

    public enum RequestStatus {
        PENDING, APPROVED, DECLINED
    }
}
