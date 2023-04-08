package com.example.server.controllers;

import com.example.server.Database;
import com.example.server.response.PosterResponse;
import com.example.server.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.sql.*;

@RestController
public class PosterController {
    Database connectionInstance;
    Connection connectionDB;
    
    public PosterController() {
        connectionInstance = Database.getInstance();
        Connection connectionDB = connectionInstance.getConnection();
    }

    @PostMapping("/Poster")
    public ResponseEntity<Response> createPoster(@RequestParam String posterName, @RequestParam MultipartFile file, @RequestParam Integer user_id, @RequestParam String room_id) {
        String stringUserId = user_id.toString();
        if (file.isEmpty()) {// Check if the image file is empty or not
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new PosterResponse("Image file is empty.", stringUserId, 0));
        }
        if (connectionInstance.checkPosterNameExist(posterName)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new PosterResponse("Poster with name: " + posterName + " is already exist", stringUserId, 0));
        }
        if(!connectionInstance.checkRoomExist(room_id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new PosterResponse("Room Id: " + room_id + " isn't exist", stringUserId, 0));
        }
        try {
            byte[] fileData = file.getBytes();
            Integer poster_id = addPosterToDB(posterName, user_id, room_id, fileData);
            if (poster_id != 0) {
                return ResponseEntity.status(HttpStatus.OK).body(new PosterResponse("Poster added successfully!", stringUserId, poster_id));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new PosterResponse("Unexpected error has occurred", stringUserId, poster_id));
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new PosterResponse("Failed to load file data.", stringUserId, 0));
        }
    }

    private void newPosterTest() throws IOException {
        // create an example file
        File file = new File("/Users/I567591/Downloads/9AD1CC86-C9EF-48C6-8083-E066B42BEAAD.jpg");

        // create a new MultipartFile instance
        MultipartFile multipartFile = new MockMultipartFile("file", "9AD1CC86-C9EF-48C6-8083-E066B42BEAAD.jpg", "image/jpg", new FileInputStream(file));

        // code to do something with the selected file
        createPoster("mai_poster", multipartFile, 1, "1");
    }

    public Integer addPosterToDB(String poster_name, Integer user_id, String room_id, byte[] file_data) {
        boolean processCompleted=false;
        String insertSql = "INSERT INTO posters (poster_name, user_id, room_id, image) VALUES (?, ?, ?, ?)";
        Integer poster_id = null;
        try {
            PreparedStatement stmt = connectionDB.prepareStatement(insertSql);
            // Bind the parameters to the prepared statement
            stmt.setString(1, poster_name);
            stmt.setInt(2, user_id);
            stmt.setString(3, room_id);
            stmt.setBytes(4, file_data);
            stmt.executeUpdate();
            processCompleted=true;

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                poster_id = rs.getInt("poster_id");
            }
            stmt.close();
        } catch (SQLException e) {
            if(processCompleted){
                //TODO
            }
        } finally {
            return poster_id;
        }
    }

}
