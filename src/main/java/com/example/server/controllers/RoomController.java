package com.example.server.controllers;

import com.example.server.Database;
import com.example.server.ServerConstants;
import com.example.server.entities.Room;
import com.example.server.response.Response;
import com.example.server.response.RoomResponse;
import com.example.server.response.AllRoomsResponse;
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
    Map<Integer, Set<Integer>> roomParticipants ; // ROOM ID 1 is default room

    public RoomController() {
        connectionDBInstance = Database.getInstance();
        connectionDB = connectionDBInstance.getConnection();
        roomParticipants = new HashMap<>();
        Set<Integer> users = new HashSet<>();
        Set<Integer> users2 = new HashSet<>();
        Set<Integer> users3 = new HashSet<>();
        users.add(1);
        users.add(2);
        users.add(3);
        users2.add(4);
        users2.add(5);
        users2.add(6);
        users3.add(7);
        users3.add(8);
        users3.add(9);
        roomParticipants.put(ServerConstants.DEFAULT_ROOM, users);
        roomParticipants.put(2, users2);
        roomParticipants.put(3, users3);
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
    @PostMapping("/getIntoRoom")
    public ResponseEntity<Response> getIntoRoom(@RequestParam Integer roomId,  @RequestParam Integer userId) {
        try {
            removeUserFromRoom(userId,ServerConstants.DEFAULT_ROOM);
            addUserToRoom(userId,roomId);
            return ResponseEntity.status(HttpStatus.OK).body(new RoomResponse(String.format(ServerConstants.USER_CHANGED_ROOM_SUCCESSFULLY, userId,roomId), roomId, getRoom(roomId)));
        } catch (Exception err){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RoomResponse(ServerConstants.UNEXPECTED_ERROR, 1, getRoom(ServerConstants.DEFAULT_ROOM)));
        }
    }
    @PostMapping("/getOutFromRoom")
    public ResponseEntity<Response> removeUserFromRoom(@RequestParam Integer userId) {
        try {
            removeUserFromRoom(userId,null);
            addUserToRoom(userId,ServerConstants.DEFAULT_ROOM);
            return ResponseEntity.status(HttpStatus.OK).body(new RoomResponse(String.format(ServerConstants.USER_CHANGED_ROOM_SUCCESSFULLY, userId,1), 1, getRoom(ServerConstants.DEFAULT_ROOM)));
        } catch (Exception err){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RoomResponse(ServerConstants.UNEXPECTED_ERROR, null, null));
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
    @GetMapping("/rooms")
    public ResponseEntity<Response> getAllRooms() {
        Map<Integer, String> roomMap = new HashMap<>();
        try {
            PreparedStatement stmt = connectionDB.prepareStatement("SELECT roomId, room_name FROM rooms");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Integer roomId = rs.getInt("roomId");
                String roomName = rs.getString("room_name");
                roomMap.put(roomId, roomName);
            }
            return ResponseEntity.status(HttpStatus.OK).body(new AllRoomsResponse(roomMap, "Message data"));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AllRoomsResponse(roomMap, "Message data"));
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
    public Integer findRoomIdByUserId(Integer userId) {
        for (Map.Entry<Integer, Set<Integer>> entry : roomParticipants.entrySet()) {
            if (entry.getValue().contains(userId)) {
                return entry.getKey();
            }
        }
        return null;
    }
    public void addUserToRoom(Integer userId, Integer roomId) {
        try {
            Set<Integer> room = roomParticipants.get(roomId);
            room.add(userId);
        } catch (Exception err) {
            throw err;
        }
    }
    public void removeUserFromRoom(Integer userId, Integer roomId) throws Exception {
        try {
            if (roomId == null) {
                for (Map.Entry<Integer, Set<Integer>> entry : roomParticipants.entrySet()) {
                    Set<Integer> participants = entry.getValue();
                    if (participants.contains(userId)) {
                        participants.remove(userId);
                        roomId = entry.getKey();
                        break;
                    }
                }
                if (roomId == null) {
                    throw new Exception("failed to get out from room!");
                }
            } else {
                Set<Integer> room = roomParticipants.get(roomId);
                room.remove(userId);
            }
        } catch (Exception err) {
            throw err;
        }
    }

//    get all avatars in room
}
