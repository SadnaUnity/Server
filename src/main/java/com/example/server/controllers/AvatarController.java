package com.example.server.controllers;
import com.example.server.Database;
import com.example.server.ServerConstants;
import com.example.server.entities.Avatar;
import com.example.server.response.AvatarResponse;
import com.example.server.response.LoginResponse;
import com.example.server.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.Arrays;
import java.util.Map;

@RestController
public class AvatarController {
    Database connectionDBInstance;
    Connection connectionDB;

    public AvatarController() {
        connectionDBInstance = Database.getInstance();
        connectionDB = connectionDBInstance.getConnection();
//        printEverything();
    }

    @PostMapping("/avatar")
    public ResponseEntity<Response> createAvatar(@RequestParam Integer userId, @RequestBody Avatar userRequestAvatar) {
        if (!connectionDBInstance.checkUserIdExist(userId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new LoginResponse(String.format(ServerConstants.USER_ID_NOT_EXISTS, userId), userId, null));
        }
        String avatarName = userRequestAvatar.getName();
        Avatar.Accessory accessory = userRequestAvatar.getAccessory();
        Avatar.Color color = userRequestAvatar.getColor();
        Avatar avatar = addNewAvatarToSystem(userId, avatarName, color, accessory);
        HttpStatus status = (avatar != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        String message = (avatar != null) ? ServerConstants.AVATAR_CREATED_SUCCESSFULLY : ServerConstants.FAILED_CREATE_AVATAR;
        return ResponseEntity.status(status).body(new AvatarResponse(String.format(message, avatarName), userId, avatar));
    }

    @GetMapping("avatar/{userId}")
    public ResponseEntity<AvatarResponse> returnAvatarData(@PathVariable Integer userId) {
        try {
            Avatar avatar = getAvatar(userId);
            if (avatar != null) {
                return ResponseEntity.ok().body(new AvatarResponse("Valid Avatar", userId, avatar));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new AvatarResponse(String.format(ServerConstants.AVATAR_NOT_EXISTS,userId), userId, null));
            }
        } catch (Exception err) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AvatarResponse(ServerConstants.UNEXPECTED_ERROR, userId, null));
        }
    }

    public Avatar getAvatar(Integer userId){
        try {
            PreparedStatement stmt = connectionDB.prepareStatement("SELECT * FROM avatars WHERE user_id = ?");
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Avatar.Accessory accessory = Avatar.Accessory.valueOf((rs.getString("accessory")));
                Avatar.Color color = Avatar.Color.valueOf(rs.getString("color"));
                String name = rs.getString("avatar_name");
                return new Avatar(accessory, color, name);
            } else {
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }
    private void printEverything(){
        try {

            // Create a SELECT statement to retrieve the data from your table
            String sql = "SELECT * FROM users";
            Statement stmt = connectionDB.createStatement();

            // Execute the SELECT statement and retrieve the ResultSet
            ResultSet rs = stmt.executeQuery(sql);

            // Loop through the ResultSet and print each row
            while (rs.next()) {
                // Get the values for each column in the current row
                int id = rs.getInt("user_id");
                String username = rs.getString("username");
                int password = rs.getInt("password");

                // Print the values to the console or to a file
                System.out.println(id + ", " + username + ", " + password);
            }

            // Close the resources
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    private Avatar addNewAvatarToSystem(Integer userId, String avatarName, Avatar.Color color, Avatar.Accessory accessory) {
        Avatar avatar = null;
        try {
            // Prepare the SQL statement with parameters
            accessory = (accessory != null) ? accessory : Avatar.Accessory.EMPTY;
            color = (color != null) ? color : Avatar.Color.RED;
            avatarName = (avatarName != null) ? avatarName : "Avatar_" + userId;
            String sql = "INSERT INTO avatars (user_id, avatar_name,  accessory, color) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connectionDB.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setString(2, avatarName);
            statement.setString(3, String.valueOf(accessory));
            statement.setString(4, String.valueOf(color));
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                avatar = new Avatar(accessory, color, avatarName);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            return avatar;
        }
    }



}
