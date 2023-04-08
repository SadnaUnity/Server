package com.example.server.controllers;

import com.example.server.Database;
import com.example.server.response.PosterResponse;
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
    Database connectionInstance;

    public RoomController() {
        connectionInstance = Database.getInstance();
        Connection connectionDB = connectionInstance.getConnection();
//        getAllColumnNames();
//        newPosterTest();
    }

    @PostMapping("/Room")
    public ResponseEntity<Response> createRoom(@RequestParam String roomName, @RequestParam String user_id) {
        if(connectionInstance.checkRoomNameExist(roomName)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RoomResponse("Poster with name: " + roomName + " is already exist", user_id, 0));
        }
        addRoomToDB(user_id);
        // Get the byte data of the uploaded file

//            addPosterToDB(nextPosterId, roomName,user_id,room_id,fileData);
        return null;
    }

    public void addRoomToDB(String manager_id) {
        String insertSql = "INSERT INTO rooms (manager_id) VALUES (?, ?)";
        try {
            PreparedStatement stmt = connectionInstance.getConnection().prepareStatement(insertSql);
            // Bind the parameters to the prepared statement

            stmt.setString(1, manager_id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




}
