package com.example.server.controllers;

import com.example.server.Database;
import com.example.server.ServerConstants;
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
    Database connectionDBInstance;
    Connection connectionDB;
    
    public PosterController() {
        connectionDBInstance = Database.getInstance();
        connectionDB = connectionDBInstance.getConnection();
//        try {
//            newPosterTest();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    @PostMapping("/poster")
    public ResponseEntity<Response> createPoster(@RequestParam String posterName, @RequestParam MultipartFile file, @RequestParam Integer userId, @RequestParam String roomId) {
        Integer posterId;
        if (file.isEmpty()) {// Check if the image file is empty or not
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new PosterResponse(ServerConstants.IMAGE_EMPTY, userId, 0));
        }
        if (connectionDBInstance.checkPosterNameExist(posterName)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new PosterResponse(String.format(ServerConstants.POSTER_EXISTS, posterName), userId, 0));
        }
        if (!connectionDBInstance.checkRoomExist(roomId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new PosterResponse(String.format(ServerConstants.ROOM_IS_NOT_EXISTS, roomId), userId, 0));
        }
        try {
            byte[] fileData = file.getBytes();
            posterId = addPosterToDB(posterName, userId, roomId, fileData);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new PosterResponse(ServerConstants.FAILED_LOAD_FILE_DATA, userId, 0));
        }
        if (posterId != 0) {
            return ResponseEntity.status(HttpStatus.OK).body(new PosterResponse(String.format(ServerConstants.POSTER_CREATED_SUCCESSFULLY, posterName), userId, posterId));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new PosterResponse(ServerConstants.UNEXPECTED_ERROR, userId, posterId));
        }
    }

    private void newPosterTest() throws IOException {
        // create an example file
        File file = new File("/Users/I567591/Downloads/9AD1CC86-C9EF-48C6-8083-E066B42BEAAD.jpg");

        // create a new MultipartFile instance
        MultipartFile multipartFile = new MockMultipartFile("file", "9AD1CC86-C9EF-48C6-8083-E066B42BEAAD.jpg", "image/jpg", new FileInputStream(file));

        // code to do something with the selected file
        createPoster("mai1_poster", multipartFile, 1, "2");
    }

    public Integer addPosterToDB(String poster_name, Integer user_id, String room_id, byte[] file_data) {
        boolean processCompleted=false;
        String insertSql = "INSERT INTO posters (poster_name, user_id, room_id, image) VALUES (?, ?, ?, ?)";
        Integer poster_id = null;
        try {
            PreparedStatement stmt = connectionDB.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, poster_name);
            stmt.setInt(2, user_id);
            stmt.setString(3, room_id);
            stmt.setBytes(4, file_data);
            stmt.executeUpdate();
            processCompleted=true;

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                poster_id = rs.getInt(1);
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
