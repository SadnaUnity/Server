package com.example.server.controllers;

import com.example.server.Database;
import com.example.server.ServerConstants;
import com.example.server.entities.Poster;
import com.example.server.entities.Room;
import com.example.server.response.PosterResponse;
import com.example.server.response.Response;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.google.cloud.storage.Blob;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.example.server.response.Response.badRequestResponse;
import static com.example.server.response.Response.serverErrorResponse;

@RestController
public class PosterController {
    Database connectionDBInstance;
    Connection connectionDB;
    Storage gcpStorage;
    public PosterController() {
        try {
            connectionDBInstance = Database.getInstance();
            connectionDB = connectionDBInstance.getConnection();
            gcpStorage = StorageOptions.newBuilder().setProjectId(ServerConstants.PROJECT_ID).setCredentials(GoogleCredentials.fromStream(new FileInputStream(ServerConstants.CREDENTIALS_PATH))).build().getService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/poster")
    public ResponseEntity<Response> createPoster(@RequestParam String posterName, @RequestParam Integer roomId, @RequestParam Integer userId, @RequestPart MultipartFile file) {
        if (file.isEmpty()) {
            return badRequestResponse(ServerConstants.IMAGE_EMPTY);
        }
        if (file.getSize() > 10485760) {
            return badRequestResponse(ServerConstants.FILE_TOO_BIG);
        }
        if (connectionDBInstance.isValueExist(ServerConstants.POSTERS_TABLE, "poster_name", posterName)) {
            return badRequestResponse(String.format(ServerConstants.POSTER_EXISTS, posterName));
        }
        if (!connectionDBInstance.isValueExist(ServerConstants.ROOMS_TABLE, "room_id", roomId)) {
            return badRequestResponse(String.format(ServerConstants.ROOM_ID_NOT_EXISTS, roomId));
        }
        if (!connectionDBInstance.isValueExist(ServerConstants.USERS_TABLE, "user_id", userId)) {
            return badRequestResponse(String.format(ServerConstants.USER_ID_NOT_EXISTS, userId));
        }
        try {
            String posterId = UUID.randomUUID().toString();
            BlobId blobId = BlobId.of(ServerConstants.BUCKET_NAME, posterId);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
            byte[] fileData = file.getBytes();
            Blob blob = gcpStorage.create(blobInfo, fileData);
            String fileUrl = blob.getMediaLink();
            Poster poster = addNewPosterToDB(posterName, userId, roomId, fileUrl);
            return ResponseEntity.status(HttpStatus.OK).body(new PosterResponse(String.format(ServerConstants.POSTER_CREATED_SUCCESSFULLY, posterName), poster.getPosterId(), poster));
        } catch (Exception err) {
            return serverErrorResponse(ServerConstants.FILE_UPLOAD_FAILED, userId, null);
        }
    }
    @GetMapping("poster/{posterId}")
    public ResponseEntity<PosterResponse> returnPosterData(@PathVariable Integer posterId) {
        if (!connectionDBInstance.isValueExist(ServerConstants.POSTERS_TABLE, "poster_id", posterId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PosterResponse(String.format(ServerConstants.POSTER_ID_NOT_EXISTS, posterId), null, null));
        }
        Poster poster = getPoster(posterId);
        if (poster != null) {
            return ResponseEntity.ok().body(new PosterResponse("Valid Poster", posterId, poster));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new PosterResponse(ServerConstants.UNEXPECTED_ERROR, posterId, null));
        }
    }

    @PostMapping("/deletePoster/{posterId}")
    public ResponseEntity<Response> deletePoster(@PathVariable("posterId") Integer posterId) {
        if (!connectionDBInstance.isValueExist(ServerConstants.POSTERS_TABLE, "poster_id", posterId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PosterResponse(String.format(ServerConstants.POSTER_ID_NOT_EXISTS, posterId), null, null));
        }
        try {
            String sql = "DELETE FROM posters WHERE poster_id = ?";
            PreparedStatement pstmt = connectionDB.prepareStatement(sql);
            pstmt.setInt(1, posterId);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted == 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new PosterResponse(String.format(ServerConstants.UNEXPECTED_ERROR, posterId), posterId, null));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(new PosterResponse(String.format(ServerConstants.POSTER_DELETED_SUCCESSFULLY, posterId), posterId, null));
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new PosterResponse(String.format(ServerConstants.UNEXPECTED_ERROR, posterId), posterId, null));
        }
    }

    private Poster addNewPosterToDB(String posterName, Integer userId, Integer roomId, String url) {
        Poster poster = null;
        try {
            String sql = "INSERT INTO posters (user_id, room_id, poster_name, url) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = connectionDB.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setInt(1, userId);
            stmt.setInt(2, roomId);
            stmt.setString(3, posterName);
            stmt.setString(4, url);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                System.out.println(rs.toString());
                if (rs.next()) {
                    Integer posterId = rs.getInt(1);
                    poster = new Poster(posterName, url, roomId, userId, posterId);
                }
            }
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Failed to add poster to database: " + e.getMessage());
        } finally {
            return poster;
        }
    }
    private Poster getPoster(Integer posterId){
        try {
            PreparedStatement stmt = connectionDB.prepareStatement("SELECT * FROM posters WHERE poster_id = ?");
            stmt.setInt(1, posterId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Integer roomId = rs.getInt("room_id");
                Integer userId = rs.getInt("user_id");
                String posterName = rs.getString("poster_name");
                String fileUrl = rs.getString("url");
                return new Poster(posterName,fileUrl,roomId,userId,posterId);
            } else {
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    private void newPosterTest() throws IOException {
        File file = new File("/Users/I567591/Downloads/9AD1CC86-C9EF-48C6-8083-E066B42BEAAD.jpg");
        MultipartFile multipartFile = new MockMultipartFile("file", "9AD1CC86-C9EF-48C6-8083-E066B42BEAAD.jpg", "image/jpg", new FileInputStream(file));
        createPoster("first poster",1,1,multipartFile);
    }


}
