package com.example.server.controllers;
import com.example.server.Database;
import com.example.server.ServerConstants;
import com.example.server.entities.Avatar;
import com.example.server.response.AvatarResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Component
public class AvatarController {
    Database connectionDBInstance;
    Connection connectionDB;
    private final ControllerManager controllerManager;

    @Autowired
    public AvatarController(@Lazy ControllerManager controllerManager, @Lazy Connection connectionDB) {
        this.controllerManager = controllerManager;
//        connectionDBInstance = Database.getInstance();
        connectionDB = connectionDB;
    }

    @PutMapping("/avatar/{avatarId}")
    public ResponseEntity<AvatarResponse> editAvatarProperties(@RequestBody Avatar userRequestAvatar, @PathVariable Integer avatarId) {
        if (!connectionDBInstance.isValueExist(ServerConstants.AVATAR_TABLE, "avatar_id", avatarId)) {
            return ResponseEntity.badRequest().body(new AvatarResponse(String.format(ServerConstants.AVATAR_NOT_EXISTS, avatarId), avatarId, null));
        }
        Map<String, Object> properties = new HashMap<>();
        properties.put("color", userRequestAvatar.getColor());
        properties.put("accessory", userRequestAvatar.getAccessory());
        boolean avatarChanged = updateAvatarsTable(properties);
        if (avatarChanged) {
            userRequestAvatar.setAvatarId(avatarId);
            return ResponseEntity.ok().body(new AvatarResponse(String.format(ServerConstants.AVATAR_UPDATED_SUCCESSFULLY), avatarId, userRequestAvatar));
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
            PreparedStatement stmt = connectionDB.prepareStatement("SELECT * FROM avatars WHERE avatar_id = ?");
            stmt.setInt(1, avatarId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Avatar.Accessory accessory = Avatar.Accessory.valueOf((rs.getString("accessory")));
                Avatar.Color color = Avatar.Color.valueOf(rs.getString("color"));
                return new Avatar(accessory, color, avatarId);
            } else {
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }
    public Avatar addNewAvatarToSystem(Integer userId, Avatar.Color color, Avatar.Accessory accessory) {
        Avatar avatar = null;
        try {
            // Prepare the SQL statement with parameters
            accessory = (accessory != null) ? accessory : Avatar.Accessory.EMPTY;
            color = (color != null) ? color : Avatar.Color.PINK;
            String sql = "INSERT INTO avatars (accessory, color, avatar_id) VALUES (?, ?, ?)";
            PreparedStatement statement = connectionDB.prepareStatement(sql);
            statement.setString(1, String.valueOf(accessory));
            statement.setString(2, String.valueOf(color));
            statement.setInt(3, userId);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                avatar = new Avatar(accessory, color, userId);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            return avatar;
        }
    }

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
