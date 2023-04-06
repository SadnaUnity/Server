package com.example.server.controllers;

import com.example.server.Database;
import com.example.server.poster.Poster;
import com.example.server.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@RestController
public class PosterController {
    Database connectionInstance;
    Connection connectionDB;
    public PosterController() {
        connectionInstance = Database.getInstance();
        Connection connectionDB = Database.getConnection();
        newTable();
    }

    @PostMapping("/Poster")
    public ResponseEntity<Response> register(@RequestParam String posterName, @RequestParam String imageUrl, @RequestParam String roomName, @RequestParam String user) {
        Poster poster = new Poster(posterName, imageUrl, roomName, user);

        return null;
    }

    public void newTable(){
//        String sql = "CREATE TABLE posters ("
//                + "poster_id INT PRIMARY KEY,"
//                + "poster_name VARCHAR(255),"
//                + "user_id INT,"
//                + "room_id INT"
////                + "FOREIGN KEY (user_id) REFERENCES users(user_id),"
////                + "FOREIGN KEY (room_id) REFERENCES rooms(room_id)"
//                + ")";
////        String sql = "ALTER TABLE users ADD COLUMN user_id INT";
//        String sql = "ALTER TABLE users ADD CONSTRAINT unique_user_id UNIQUE (user_id)";
//        String sql = "ALTER TABLE posters ADD COLUMN user_id INT";
        String sql = "ALTER TABLE posters ADD CONSTRAINT unique_user_id FOREIGN KEY (user_id) REFERENCES users (user_id)";


        try {
            Connection connection = connectionInstance.getConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
//    public void addPoster(Poster poster) {
//        String sql = "INSERT INTO posters (name, image_url) VALUES (?, ?)";
//        PreparedStatement stmt = conn.prepareStatement(sql);
//        stmt.setString(1, poster.getName());
//        stmt.setString(2, poster.getImageUrl());
//        stmt.executeUpdate();
//    }
}
