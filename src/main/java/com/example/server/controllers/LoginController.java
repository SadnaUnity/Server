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
    private String databaseName ="sadnaDB";
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
            throw new RuntimeException(e);
        }
        // try
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        if (checkValidUser(username, password)) {
            return ResponseEntity.ok("Login successfully");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    public boolean checkValidUser(String username, String password) {
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
}

