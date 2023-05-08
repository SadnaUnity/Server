package com.example.server.controllers;

import com.example.server.Database;
import com.example.server.ServerConstants;
import com.example.server.entities.Room;
import com.example.server.response.AvatarResponse;
import com.example.server.response.PosterResponse;
import com.example.server.response.Response;
import com.example.server.response.RoomResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.*;

@RestController
@Singleton
public class RoomController {
    Database connectionDBInstance;
    Connection connectionDB;
    Map<Integer, Set<Integer>> roomParticipants ;
    //TODO: ADD avatar to room map
    public RoomController() {
        connectionDBInstance = Database.getInstance();
        connectionDB = connectionDBInstance.getConnection();
        roomParticipants = new HashMap<>();
        Set<Integer> users = new HashSet<>();
        users.add(1);
        users.add(2);
        roomParticipants.put(1, users);
        roomParticipants.put(3, new HashSet<>(Collections.singletonList(3)));

    }
    @PostMapping("/room")
    public ResponseEntity<Response> createRoom(@RequestParam String roomName,  @RequestBody Room userRequestRoom) {
        int maxCapacity = userRequestRoom.getMaxCapacity();
        boolean privacy = userRequestRoom.isPrivacy();
        int managerId = userRequestRoom.getManagerId();
        if (connectionDBInstance.isValueExist(ServerConstants.ROOMS_TABLE,"room_name",roomName)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RoomResponse(String.format(ServerConstants.ROOM_EXISTS, roomName), null, null));
        }
        if (!connectionDBInstance.isValueExist(ServerConstants.USERS_TABLE,"user_id",managerId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RoomResponse(String.format(ServerConstants.ROOM_EXISTS, roomName), null, null));
        }
        Room room = addNewRoomToDB(roomName, managerId, privacy, maxCapacity);
        if (room != null) {
            return ResponseEntity.status(HttpStatus.OK).body(new RoomResponse(String.format(ServerConstants.ROOM_CREATED_SUCCESSFULLY, roomName), room.getRoomId(), room));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RoomResponse(ServerConstants.UNEXPECTED_ERROR, null, room));
        }
    }
    @GetMapping("/room/{roomId}")
    public ResponseEntity<Response> getRoomByRoomId(@PathVariable Integer roomId) {
        if (!connectionDBInstance.isValueExist(ServerConstants.ROOMS_TABLE,"room_id",roomId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RoomResponse(String.format(ServerConstants.ROOM_ID_NOT_EXISTS, roomId), null, null));
        }
        try {
            Room room = getRoom(roomId);
            return ResponseEntity.status(HttpStatus.OK).body(new RoomResponse("Completed successfully", roomId, room));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RoomResponse(ServerConstants.UNEXPECTED_ERROR, roomId, null));
        }
    }
    @PostMapping("/deleteRoom/{roomId}")
    public ResponseEntity<Response> deleteRoom(@PathVariable("roomId") Integer roomId) { //delete all posters !!!!
        if (!connectionDBInstance.isValueExist(ServerConstants.ROOMS_TABLE, "room_id", roomId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RoomResponse(String.format(ServerConstants.ROOM_ID_NOT_EXISTS, roomId), null, null));
        }
        try {
            String sql = "DELETE FROM rooms WHERE room_id = ?";
            PreparedStatement pstmt = connectionDB.prepareStatement(sql);
            pstmt.setInt(1, roomId);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted == 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RoomResponse(String.format(ServerConstants.UNEXPECTED_ERROR, roomId), roomId, null));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(new RoomResponse(String.format(ServerConstants.ROOM_DELETED_SUCCESSFULLY, roomId), roomId, null));
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RoomResponse(String.format(ServerConstants.UNEXPECTED_ERROR, roomId), roomId, null));
        }
    }
    public Room getRoom(Integer roomId) {
        try {
            PreparedStatement stmt = connectionDB.prepareStatement("SELECT * FROM rooms WHERE room_id = ?");
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Boolean privacy = rs.getBoolean("privacy");
                Integer maxCapacity = (rs.getInt("max_capacity"));
                Integer managerId = rs.getInt("manager_id");
                String roomName = rs.getString("room_name");
                return new Room(privacy, managerId, maxCapacity, roomId, roomName);
            } else {
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }
    public Room addNewRoomToDB(String roomName, Integer managerId, boolean privacy, int maxCapacity){
        String insertSql = "INSERT INTO rooms (manager_id, room_name, privacy, max_capacity) VALUES (?,?,?,?)";
        Integer roomId = null;
        Room room = null;
        boolean roomCreated = false;
        try {
            PreparedStatement stmt = connectionDB.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, managerId);
            stmt.setString(2, roomName);
            stmt.setBoolean(3, privacy);
            stmt.setInt(4, maxCapacity);
            stmt.executeUpdate();
            roomCreated = true;

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                roomId = rs.getInt(1);
                room = new Room(privacy,managerId,maxCapacity,roomId,roomName);
            }
            stmt.close();
        } catch (SQLException e) {
            if (roomCreated) {
                //TODO handle
            }
        } finally {
            return room;
        }
    }

    public Integer getRoomId(Integer userId) {
        for (Map.Entry<Integer, Set<Integer>> entry : roomParticipants.entrySet()) {
            if (entry.getValue().contains(userId)) {
                return entry.getKey();
            }
        }
        return null;
    }

}
