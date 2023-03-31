package com.example.server.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;

@RestController
public class LoginController {

    private String jdbcUrl;
    private String databaseName;
    private String instanceConnectionName;
    private String dbUsername;
    private String dbPassword;

    public LoginController()
    {
        databaseName = "sadnaDB";
        instanceConnectionName = "sadna-db-server";
        dbUsername = "root";
        dbPassword = "Chxkhcmk69";

        jdbcUrl = String.format(
                "jdbc:mysql://34.165.31.251:3306/%s?cloudSqlInstance=%s&socketFactory=com.google.cloud.sql.mysql.SocketFactory",
                databaseName, instanceConnectionName);
    }
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        if(checkUserExists(username, password))
            return "GOOD";
        else
            return "bad";

    }
    public boolean checkUserExists(String username, String password) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();

        } catch (SQLException e) {
            System.err.println("Failed to check if user exists: " + e.getMessage());
            return false;
        }
    }

}
