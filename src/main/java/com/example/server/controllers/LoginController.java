package com.example.server.controllers;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.sql.*;

@RestController
public class LoginController {

    Connection connectionDB;
    private String databaseName;
    private String dbUsername;
    private String dbPassword;
    private String dbHostAddress;
    private String dbHostPort;

    public LoginController() {
//        instanceConnectionName = "sadna-db-server";
        databaseName = "sadnaDB";
        dbUsername = "root";
        dbPassword = "Chxkhcmk69";
        dbHostAddress = "34.165.31.251";
        dbHostPort = "3306";
        String jdbcUrl = "jdbc:mysql://" + dbHostAddress + ":" + dbHostPort + "/" + databaseName;

//        jdbcUrl = String.format(
//                "jdbc:mysql://34.165.31.251:3306/%s?cloudSqlInstance=%s&socketFactory=com.google.cloud.sql.mysql.SocketFactory",
//                databaseName, instanceConnectionName);
        try {
            connectionDB = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
            //TODO: connectionDB.close();

        } catch (SQLException e) {
            //TODO: handle exception
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        if (checkValidUser(username, password))
            return "valid";
        else
            return "invalid";
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

