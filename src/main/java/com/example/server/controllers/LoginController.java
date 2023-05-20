package com.example.server.controllers;
import com.example.server.Database;
import com.example.server.ServerConstants;
import com.example.server.entities.Avatar;
import com.example.server.response.LoginResponse;
import com.example.server.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.sql.*;

@RestController
@Component
public class LoginController {
    Database connectionDBInstance;
    Connection connectionDB;

    private final ControllerManager controllerManager;

    @Autowired
    public LoginController(@Lazy ControllerManager controllerManager) {
        this.controllerManager = controllerManager;
        connectionDBInstance = Database.getInstance();
        connectionDB = connectionDBInstance.getConnection();
    }
    @PostMapping("/logout")
    public ResponseEntity<Response> logout(@RequestParam Integer userId) {
        controllerManager.logout(userId);
        return ResponseEntity.status(HttpStatus.OK).body(new LoginResponse(ServerConstants.LOGOUT_MESSAGE, userId, null, null));
    }
    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestParam String username, @RequestParam String password) {
        Integer userId = connectionDBInstance.checkValidUserDetailsLogin(username.trim(), password.trim());
        if (userId != 0) {
            if (controllerManager.isUserOnline(userId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new LoginResponse(String.format(ServerConstants.USER_ALREADY_ONLINE, username), userId, username, null));
            }
            controllerManager.addUserToRoom(userId, ServerConstants.DEFAULT_ROOM);
            return ResponseEntity.status(HttpStatus.OK).body(new LoginResponse(ServerConstants.LOGIN_SUCCESSFULLY, userId, username, controllerManager.getAvatar(userId)));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse(ServerConstants.INVALID_USERNAME_OR_PASSWORD, 0, null, null));
        }
    }
    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestParam String username, @RequestParam String password, @RequestParam Integer avatarColor, @RequestParam Integer avatarAccessory) {
        if (connectionDBInstance.isValueExist(ServerConstants.USERS_TABLE,"username",username)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new LoginResponse(String.format(ServerConstants.USER_EXISTS, username),null, null, null));
        } else if (!isValidUserName(username) || !isValidPassword(password)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new LoginResponse(ServerConstants.INVALID_USERNAME_OR_PASSWORD, null, null,null));
        }
        Avatar avatar = null;
        Integer userId = createNewUserInSystem(username, password);
        if (userId != 0) {
            avatar = controllerManager.addNewAvatarToSystem(userId, Avatar.Color.values()[avatarColor], Avatar.Accessory.values()[avatarAccessory]);
            controllerManager.addUserToRoom(userId,ServerConstants.DEFAULT_ROOM);
        }
        HttpStatus status = userId != null ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
        String message = userId != null ? String.format(ServerConstants.USER_CREATED_SUCCESSFULLY, username) : ServerConstants.UNEXPECTED_ERROR;
        return ResponseEntity.status(status).body(new LoginResponse(message, userId,username,  avatar));
    }
    private boolean isValidUserName(String userName) {
        if (userName == null || userName.trim().length() == 0) {
            return false;
        }
        return userName.matches("^[a-zA-Z0-9]+$");
    }
    private boolean isValidPassword(String password) {
        if (password == null || password.trim().length() == 0) {
            return false;
        }
        return password.matches("[a-zA-Z0-9]+"); // Password contains only letters and digits
    }
    private Integer createNewUserInSystem(String username, String password) {
        Integer user_id = null;
        boolean userCreated = false;
        try {
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement stmt = connectionDB.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            userCreated = true;

            // Get the generated user_id value
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                user_id = rs.getInt(1);
            }
            stmt.close();
        } catch (SQLException e) {
            if(userCreated){
                //TODO handle case
            }
        } finally {
            return user_id;
        }
    }

}
