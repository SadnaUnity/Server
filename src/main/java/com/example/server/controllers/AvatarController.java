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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

//    @PostMapping("/avatar")
//    public ResponseEntity<Response> createAvatar(@RequestParam Integer userId, @RequestBody Avatar userRequestAvatar) {
//        if (!connectionDBInstance.isValueExist(ServerConstants.USERS_TABLE,"user_id",userId)) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new LoginResponse(String.format(ServerConstants.USER_ID_NOT_EXISTS, userId), userId,userId, null));
//        }
//        String avatarName = userRequestAvatar.getName();
//        Avatar.Accessory accessory = userRequestAvatar.getAccessory();
//        Avatar.Color color = userRequestAvatar.getColor();
//        Avatar avatar = addNewAvatarToSystem(userId, avatarName, color, accessory);
//        HttpStatus status = (avatar != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
//        String message = (avatar != null) ? ServerConstants.AVATAR_CREATED_SUCCESSFULLY : ServerConstants.FAILED_CREATE_AVATAR;
//        return ResponseEntity.status(status).body(new AvatarResponse(String.format(message, avatarName), userId, avatar));
//    }
    @PutMapping("/avatar/{avatarId}")
    public ResponseEntity<AvatarResponse> editAvatarProperties(@RequestBody Avatar userRequestAvatar, @PathVariable Integer avatarId) {
        if (!connectionDBInstance.isValueExist(ServerConstants.AVATAR_TABLE, "avatar_id", avatarId)) {
            return ResponseEntity.badRequest().body(new AvatarResponse(String.format(ServerConstants.AVATAR_NOT_EXISTS, avatarId), avatarId, null));
        }
        Map<String, Object> properties = new HashMap<>();
        properties.put("avatar_name", userRequestAvatar.getName());
        properties.put("color", userRequestAvatar.getColor());
        properties.put("accessory", userRequestAvatar.getAccessory());
        boolean avatarChanged = updateAvatarsTable(properties);
        if (avatarChanged) {
            userRequestAvatar.setAvatarId(avatarId);
            return ResponseEntity.ok().body(new AvatarResponse("Avatar properties updated successfully", avatarId, userRequestAvatar));
        } else {
            return ResponseEntity.badRequest().body(new AvatarResponse("Failed to update avatar properties", avatarId, null));
        }
    }

    @GetMapping("avatar/{avatarId}")
    public ResponseEntity<AvatarResponse> returnAvatarData(@PathVariable Integer avatarId) {
        try {
            Avatar avatar = getAvatar(avatarId);
            if (avatar != null) {
                return ResponseEntity.ok().body(new AvatarResponse("Valid Avatar", avatarId, avatar));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new AvatarResponse(String.format(ServerConstants.AVATAR_NOT_EXISTS,avatarId), avatarId, null));
            }
        } catch (Exception err) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AvatarResponse(ServerConstants.UNEXPECTED_ERROR, avatarId, null));
        }
    }

    public Avatar getAvatar(Integer avatarId){
        try {
            PreparedStatement stmt = connectionDB.prepareStatement("SELECT * FROM avatars WHERE user_id = ?");
            stmt.setInt(1, avatarId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Avatar.Accessory accessory = Avatar.Accessory.valueOf((rs.getString("accessory")));
                Avatar.Color color = Avatar.Color.valueOf(rs.getString("color"));
                String name = rs.getString("avatar_name");
                return new Avatar(accessory, color, name, avatarId);
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
    public Avatar addNewAvatarToSystem(Integer userId, String avatarName, Avatar.Color color, Avatar.Accessory accessory) {
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
                avatar = new Avatar(accessory, color, avatarName, userId);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            return avatar;
        }
    }

//    private Avatar updateAvatarsTable(Integer avatarId, String avatarName, Avatar.Color color, Avatar.Accessory accessory){
//       Avatar avatar = null;
//        try {
//            String sql = "UPDATE avatars SET " + columnToUpdate + " = ? WHERE " + conditionColumn + " = ?";
//            PreparedStatement statement = connectionDB.prepareStatement(sql);
//            statement.setObject(1, newValue);
//            statement.setObject(2, conditionValue);
//            int numRowsAffected = statement.executeUpdate();
//            System.out.println(numRowsAffected + " row(s) updated in " + tableName);
//            statement.close();
//        } catch (SQLException e) {
//            System.out.println("Error: " + e.getMessage());
//        } finally {
//            return avatar;
//        }
//    }
    public boolean updateAvatarsTable(Map<String, Object> properties) {
        boolean updatedSuccessfully=false;
        try {
            // Construct UPDATE statement based on the properties
            StringBuilder sql = new StringBuilder("UPDATE avatars SET ");
            List<Object> values = new ArrayList<>();
            for (String key : properties.keySet()) {
                Object value = properties.get(key);
                if (value != null) {
                    sql.append(key).append(" = ?, ");
                    values.add(value);
                }
            }
            // Remove the trailing comma and space from the SQL string
            sql.delete(sql.length() - 2, sql.length());
            // Execute the SQL statement
            PreparedStatement statement = connectionDB.prepareStatement(sql.toString());
            for (int i = 0; i < values.size(); i++) {
                System.out.println(values.get(i).toString());
                statement.setObject(i + 1, values.get(i).toString());
            }
            statement.executeUpdate();
            statement.close();
            updatedSuccessfully=true;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            return updatedSuccessfully;
        }
    }




}
