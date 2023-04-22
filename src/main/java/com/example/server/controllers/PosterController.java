package com.example.server.controllers;

import com.example.server.Database;
import com.example.server.ServerConstants;
import com.example.server.entities.Avatar;
import com.example.server.entities.Poster;
import com.example.server.response.AvatarResponse;
import com.example.server.response.PosterResponse;
import com.example.server.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<Response> createPoster(@RequestParam String posterName, @RequestParam MultipartFile file, @RequestParam Integer userId, @RequestParam Integer roomId) {
        Integer posterId;
        if (file.isEmpty()) {// Check if the image file is empty or not
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new PosterResponse(ServerConstants.IMAGE_EMPTY, null, null));
        }
        if (connectionDBInstance.isPosterNameExist(posterName)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new PosterResponse(String.format(ServerConstants.POSTER_EXISTS, posterName), null, null));
        }
        if (!connectionDBInstance.isRoomIdExist(roomId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new PosterResponse(String.format(ServerConstants.ROOM_ID_NOT_EXISTS, roomId), null, null));
        }
//        try {
//            byte[] fileData = file.getBytes();
//            posterId = addPosterToDB(posterName, userId, roomId, fileData);
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new PosterResponse(ServerConstants.FAILED_LOAD_FILE_DATA, null, null));
//        }
//        if (posterId != 0) {
//            return ResponseEntity.status(HttpStatus.OK).body(new PosterResponse(String.format(ServerConstants.POSTER_CREATED_SUCCESSFULLY, posterName), userId, posterId));
//        } else {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new PosterResponse(ServerConstants.UNEXPECTED_ERROR, userId, posterId));
//        }
        return null;
    }

    private void newPosterTest() throws IOException {
        // create an example file
        File file = new File("/Users/I567591/Downloads/9AD1CC86-C9EF-48C6-8083-E066B42BEAAD.jpg");

        // create a new MultipartFile instance
        MultipartFile multipartFile = new MockMultipartFile("file", "9AD1CC86-C9EF-48C6-8083-E066B42BEAAD.jpg", "image/jpg", new FileInputStream(file));

        // code to do something with the selected file
        createPoster("mai1_poster", multipartFile, 1, 2);
    }

//    @GetMapping("poster/{posterId}")
//    public ResponseEntity<PosterResponse> returnPosterData(@PathVariable Integer posterId) {
//        if(!connectionDBInstance.isPosterIdExist(posterId)){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PosterResponse(String.format(ServerConstants.POSTER_ID_NOT_EXISTS,posterId), null, null));
//        }
//        try {
//            Avatar poster = getPoster(posterId);
//            if (poster != null) {
//                return ResponseEntity.ok().body(new PosterResponse("Valid Poster", posterId, poster));
//            } else {
//            }
//        } catch (Exception err) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AvatarResponse(ServerConstants.UNEXPECTED_ERROR, userId, null));
//        }
//    }
//    public Poster addPosterToDB(String posterName, Integer userId, Integer roomId, byte[] fileData) {
//        boolean processCompleted=false;
//        String insertSql = "INSERT INTO posters (poster_name, user_id, room_id, image) VALUES (?, ?, ?, ?)";
//        Integer poster_id = null;
//        Poster poster=null;
//        try {
//            PreparedStatement stmt = connectionDB.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
//            stmt.setString(1, posterName);
//            stmt.setInt(2, userId);
//            stmt.setInt(3, roomId);
//            stmt.setBytes(4, fileData);
//            stmt.executeUpdate();
//            processCompleted=true;
//
//            ResultSet rs = stmt.getGeneratedKeys();
//            if (rs.next()) {
//                poster_id = rs.getInt(1);
//                poster=new Poster(posterName,fileData,roomId,userId);
//            }
//            stmt.close();
//        } catch (SQLException e) {
//            if(processCompleted){
//                //TODO
//            }
//        } finally {
//            return poster;
//        }
//    }
//    private Poster getPoster(Integer posterId){
//        try {
//            PreparedStatement stmt = connectionDB.prepareStatement("SELECT * FROM posters WHERE poster_id = ?");
//            stmt.setInt(1, posterId);
//            ResultSet rs = stmt.executeQuery();
//            if (rs.next()) {
//                Avatar.Accessory accessory = Avatar.Accessory.valueOf((rs.getString("accessory")));
//                String url = Avatar.Color.valueOf(rs.getString("color"));
//                String name = rs.getString("poster_name");
//                return new Avatar(accessory, color, name);
//            } else {
//                return null;
//            }
//        } catch (SQLException e) {
//            return null;
//        }
//    }

}
