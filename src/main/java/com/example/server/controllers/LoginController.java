package com.example.server.controllers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.sql.*;

@RestController
public class LoginController {
    Connection connectionDB;
    private String databaseName = "sadnaDB";
    private String dbUsername = "root";
    private String dbPassword = "Chxkhcmk69";
    private String dbHostAddress = "34.165.31.251";
    private String dbHostPort = "3306";

    public LoginController() {
        String jdbcUrl = "jdbc:mysql://" + dbHostAddress + ":" + dbHostPort + "/" + databaseName;
        try {
            connectionDB = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
            //TODO: connectionDB.close();
        } catch (SQLException e) {
            //TODO: handle exception
            // throw new RuntimeException(e);
        }
        // try
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestParam String username, @RequestParam String password) {
        if (checkValidUserDetailsLogin(username.trim(), password.trim())) {
            return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("Login successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("The username or password you entered is not correct."));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestParam String username, @RequestParam String password, @RequestParam String email) {
        if (checkUserExist(username)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Username '" + username + "' is already exists"));
        }
        if (!isValidUserName(username)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Invalid user name!"));
        }
        if (!isValidPassword(password)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Invalid user password!"));
        }
        if (createNewUserInSystem(username, password, email)) {
            return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("User created successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Unexpected error has occurred"));
        }
    }

    private boolean checkValidUserDetailsLogin(String username, String password) {
        boolean validUser = false;
        try {
            String sql = "SELECT * FROM users"; //Prepare the SQL statement
            Statement stmt = connectionDB.createStatement();
            ResultSet dbResponse = stmt.executeQuery(sql); // Execute the SQL statement and get the results

            while (dbResponse.next()) {
                if (dbResponse.getString("username").equals(username)) {
                    if (dbResponse.getString("password").equals(password))
                        validUser = true;
                }
            }

            dbResponse.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return validUser;
    }

    private boolean checkUserExist(String username) {
        boolean userExist = false;
        try {
            String sql = "SELECT username FROM users"; //Prepare the SQL statement
            Statement stmt = connectionDB.createStatement();
            ResultSet dbResponse = stmt.executeQuery(sql); // Execute the SQL statement and get the results

            while (dbResponse.next()) {
                System.out.println(dbResponse.getString("username"));
                if (dbResponse.getString("username").equals(username)) {
                    userExist = true;
                    break;
                }
            }

            dbResponse.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return userExist;
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

    private boolean createNewUserInSystem(String username, String password, String email) {
        PreparedStatement stmt = null;
        boolean userCreated = false;
        try {
//            String query = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            stmt = connectionDB.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
//            stmt.setString(3, email);
            stmt.executeUpdate(); // Execute the INSERT statement
            userCreated=true;
        } catch (SQLException e) {
            return userCreated;
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return userCreated;
            }
        }
        return userCreated;
    }
}
