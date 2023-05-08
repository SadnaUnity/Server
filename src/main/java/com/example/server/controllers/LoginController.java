package com.example.server.controllers;
import com.example.server.Database;
import com.example.server.ServerConstants;
import com.example.server.entities.Avatar;
import com.example.server.response.LoginResponse;
import com.example.server.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;

@RestController
public class LoginController {
    Database connectionDBInstance;
    Connection connectionDB;
    AvatarController avatarController;
    public LoginController() {
        connectionDBInstance = Database.getInstance();
        connectionDB = connectionDBInstance.getConnection();
        avatarController = new AvatarController();
//        printEverything();
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestParam String username, @RequestParam String password) {
        Integer userId = connectionDBInstance.checkValidUserDetailsLogin(username.trim(), password.trim());
        if (userId != 0) {
            return ResponseEntity.status(HttpStatus.OK).body(new LoginResponse(ServerConstants.LOGIN_SUCCESSFULLY, userId,username,userId, avatarController.getAvatar(userId)));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse(ServerConstants.INVALID_USERNAME_OR_PASSWORD, null,null, null,null));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestParam String username, @RequestParam String password, @RequestParam String avatarColor, @RequestParam String avatarAccessory) {
        if (connectionDBInstance.isValueExist(ServerConstants.USERS_TABLE,"username",username)) {
            return ResponseEntity.status(ServerConstants.BAD_REQUEST_RESPONSE_CODE).body(new LoginResponse(String.format(ServerConstants.USER_EXISTS, username),null, null,null, null));
        } else if (!isValidUserName(username) || !isValidPassword(password)) {
            return ResponseEntity.status(ServerConstants.BAD_REQUEST_RESPONSE_CODE).body(new LoginResponse(ServerConstants.INVALID_USERNAME_OR_PASSWORD, null, null,null,null));
        }
        Avatar avatar = null;
        Integer user_id = createNewUserInSystem(username, password);
        if (user_id != 0) {
            avatar = avatarController.addNewAvatarToSystem(user_id, username, Avatar.Color.values()[Integer.parseInt(avatarColor)], Avatar.Accessory.values()[Integer.parseInt(avatarAccessory)]);
        }
        HttpStatus status = user_id != null ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
        String message = user_id != null ? String.format(ServerConstants.USER_CREATED_SUCCESSFULLY, username) : ServerConstants.UNEXPECTED_ERROR;
        return ResponseEntity.status(status).body(new LoginResponse(message, user_id,username, avatar.getAvatarId(), avatar));
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

}
