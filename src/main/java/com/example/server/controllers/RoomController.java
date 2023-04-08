package com.example.server.controllers;

import com.example.server.Database;
import com.example.server.ServerConstants;
import com.example.server.response.Response;
import com.example.server.response.RoomResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;

@RestController
public class RoomController {
    Database connectionDBInstance;
    Connection connectionDB;
    public RoomController() {
        connectionDBInstance = Database.getInstance();
        connectionDB = connectionDBInstance.getConnection();
    }

    @PostMapping("/room")
    public ResponseEntity<Response> createRoom(@RequestParam String roomName, @RequestParam Integer userId) {
        if (connectionDBInstance.checkRoomNameExist(roomName)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RoomResponse(String.format(ServerConstants.ROOM_EXISTS, roomName), userId, null));
        }
        Integer roomId = addRoomToDB(roomName,userId);
        if (roomId != null) {
            return ResponseEntity.status(HttpStatus.OK).body(new RoomResponse(String.format(ServerConstants.ROOM_CREATED_SUCCESSFULLY,roomName), userId, roomId));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RoomResponse(ServerConstants.UNEXPECTED_ERROR, userId, null));
        }
    }

    public Integer addRoomToDB(String roomName,Integer manager_id) {
        String insertSql = "INSERT INTO rooms (manager_id, room_name) VALUES (?,?)";
        Integer room_id= null;
        boolean roomCreated=false;
        try {
            PreparedStatement stmt = connectionDB.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, manager_id);
            stmt.setString(2, roomName);
            stmt.executeUpdate();
            roomCreated=true;

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                room_id = rs.getInt(1);
            }
            stmt.close();
        } catch (SQLException e) {
            if(roomCreated){
                //TODO handle
            }
        }finally {
            return room_id;
        }
    }

}
