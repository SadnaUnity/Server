package com.example.server;

import java.sql.*;

public class Database {
    private static Connection connectionDB;
    private static Database instance = null;
    private String databaseName = "sadnaDB";
    private String dbUsername = "root";
    private String dbPassword = "Chxkhcmk69";
    private String dbHostAddress = "34.165.31.251";
    private String dbHostPort = "3306";
    private Database() {
        String jdbcUrl = "jdbc:mysql://" + dbHostAddress + ":" + dbHostPort + "/" + databaseName;
        try {
            connectionDB = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
            //TODO: connectionDB.close();
        } catch (SQLException e) {
            System.out.println("Error creating database connection: " + e.getMessage());
        }
    }
    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public static Connection getConnection() {
        return connectionDB;
    }

    // other database-related methods...
}
