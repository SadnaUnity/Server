package com.example.server.controllers;

import com.example.server.Database;
import com.example.server.ServerConstants;
import com.example.server.entities.Poster;
import com.example.server.entities.Room;
import com.example.server.response.Response;
import com.example.server.response.RoomResponse;
import com.example.server.response.AllRoomsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.*;

@RestController
@Component
public class RoomController {
    Database connectionDBInstance;
    Connection connectionDB;
    Map<Integer, Set<Integer>> roomParticipants; // ROOM ID 1 is default room
    private final ControllerManager controllerManager;

    @Autowired
    public RoomController(@Lazy ControllerManager controllerManager) {
        this.controllerManager = controllerManager;
        connectionDBInstance = Database.getInstance();
        connectionDB = connectionDBInstance.getConnection();
        roomParticipants = new HashMap<>();
        roomParticipants.put(ServerConstants.DEFAULT_ROOM, new HashSet<>());

    }
    @PostMapping("/room")
    public ResponseEntity<Response> createRoom(@RequestParam String roomName, @RequestBody Room userRequestRoom) {
        int maxCapacity = userRequestRoom.getMaxCapacity();
        boolean privacy = userRequestRoom.isPrivacy();
        int managerId = userRequestRoom.getManagerId();
        if (connectionDBInstance.isValueExist(ServerConstants.ROOMS_TABLE, "room_name", roomName)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RoomResponse(String.format(ServerConstants.ROOM_EXISTS, roomName), null, null));
        }
        if (!connectionDBInstance.isValueExist(ServerConstants.USERS_TABLE, "user_id", managerId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RoomResponse(String.format(ServerConstants.ROOM_EXISTS, roomName), null, null));
        }
        Room room = addNewRoomToDB(roomName, managerId, privacy, maxCapacity);
        if (room != null) {
            roomParticipants.put(room.getRoomId(),new HashSet<>());
            return ResponseEntity.status(HttpStatus.OK).body(new RoomResponse(String.format(ServerConstants.ROOM_CREATED_SUCCESSFULLY, roomName), room.getRoomId(), room));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RoomResponse(ServerConstants.UNEXPECTED_ERROR, null, room));
        }
    }
    @PostMapping("/getIntoRoom")
    public ResponseEntity<Response> getIntoRoom(@RequestParam Integer roomId, @RequestParam Integer userId) {
        try {
            removeUserFromRoom(userId);
            insertUserIdIntoRoom(userId, roomId);
            controllerManager.removeUserFromChatRoom(ServerConstants.DEFAULT_ROOM);
            controllerManager.addUserIntoChatRoom(userId,roomId);
            return ResponseEntity.status(HttpStatus.OK).body(new RoomResponse(String.format(ServerConstants.USER_CHANGED_ROOM_SUCCESSFULLY, userId, roomId), roomId, getRoomDetails(roomId)));
        } catch (Exception err) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RoomResponse(ServerConstants.UNEXPECTED_ERROR, 1, getRoomDetails(ServerConstants.DEFAULT_ROOM)));
        }
    }
    @PostMapping("/getOutFromRoom")
    public ResponseEntity<Response> getOutFromRoom(@RequestParam Integer userId) {
        try {
            removeUserFromRoom(userId);
            insertUserIdIntoRoom(userId, ServerConstants.DEFAULT_ROOM);
            controllerManager.removeUserFromChatRoom(userId);
            return ResponseEntity.status(HttpStatus.OK).body(new RoomResponse(String.format(ServerConstants.USER_CHANGED_ROOM_SUCCESSFULLY, userId, 1), 1, getRoomDetails(ServerConstants.DEFAULT_ROOM)));
        } catch (Exception err) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RoomResponse(ServerConstants.UNEXPECTED_ERROR, null, null));
        }
    }
    @GetMapping("/room/{roomId}")
    public ResponseEntity<Response> getRoomByRoomId(@PathVariable Integer roomId) {
        if (!connectionDBInstance.isValueExist(ServerConstants.ROOMS_TABLE, "room_id", roomId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RoomResponse(String.format(ServerConstants.ROOM_ID_NOT_EXISTS, roomId), null, null));
        }
        try {
            Room room = getRoomDetails(roomId);
            return ResponseEntity.status(HttpStatus.OK).body(new RoomResponse("Completed successfully", roomId, room));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RoomResponse(ServerConstants.UNEXPECTED_ERROR, roomId, null));
        }
    }
    @GetMapping("/rooms")
    public ResponseEntity<Response> getAllRooms() {
        Map<Integer, String> roomMap = new HashMap<>();
        try {
            PreparedStatement stmt = connectionDB.prepareStatement("SELECT room_id, room_name FROM rooms");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Integer roomId = rs.getInt("room_id");
                String roomName = rs.getString("room_name");
                roomMap.put(roomId, roomName);
            }
            return ResponseEntity.status(HttpStatus.OK).body(new AllRoomsResponse(roomMap, "Message data"));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AllRoomsResponse(roomMap, ServerConstants.UNEXPECTED_ERROR));
        }

//        try {
//            Room room = getRoom(roomId);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RoomResponse(ServerConstants.UNEXPECTED_ERROR, roomId, null));
//        }
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
    public Room getRoomDetails(Integer roomId) {
        Boolean privacy;
        Integer maxCapacity;
        Integer managerId;
        String roomName;
        try {
            PreparedStatement stmt = connectionDB.prepareStatement("SELECT * FROM rooms WHERE room_id = ?");
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                privacy = rs.getBoolean("privacy");
                maxCapacity = (rs.getInt("max_capacity"));
                managerId = rs.getInt("manager_id");
                roomName = rs.getString("room_name");
            } else {
                return null;
            }
            List<Poster> allPostersInRoom = controllerManager.getAllPostersInRoom(roomId);
            return new Room(privacy, managerId, maxCapacity, roomId, roomName, allPostersInRoom);
        } catch (SQLException e) {
            return null;
        }
    }
    public Room addNewRoomToDB(String roomName, Integer managerId, boolean privacy, int maxCapacity) {
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
                room = new Room(privacy, managerId, maxCapacity, roomId, roomName,null);
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
    public void insertUserIdIntoRoom(Integer userId, Integer roomId) {
        try {
            Set<Integer> room = roomParticipants.get(roomId);
            if (room == null)
            {
                room = new HashSet<Integer>();
                roomParticipants.put(roomId, room);
            }

            room.add(userId);

        } catch (Exception err) {
            throw err;
        }
    }
    public void removeUserFromRoom(Integer userId) {
        for (Map.Entry<Integer, Set<Integer>> room : roomParticipants.entrySet()) {
            Set<Integer> participants = room.getValue();
            if (participants.contains(userId)) {
                participants.remove(userId);
                break;
            }
        }
    }
    public Set<Integer> getAllUsersInRoom(Integer roomId) {
        return roomParticipants.get(roomId);
    }
    public boolean isUserOnline(Integer userId) {
        for (Set<Integer> room : roomParticipants.values()) {
            if (room != null && room.contains(userId)) {
                return true; // User is online in at least one room
            }
        }
        return false; // User is not online in any room
    }
}
