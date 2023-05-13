package com.example.server;

import java.sql.*;

public class Database {
    private static Connection connectionDB;
    private static Database instance = null;

    private Database() {
        String jdbcUrl = "jdbc:mysql://" + ServerConstants.DB_HOST + ":" + ServerConstants.DB_HOST_PORT + "/" + ServerConstants.DATABASE_NAME;
        try {
            connectionDB = DriverManager.getConnection(jdbcUrl, ServerConstants.DB_USER_NAME, ServerConstants.DB_PASSWORD);
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

    public boolean isValueExist(String tableName, String columnName, Object value) {
        boolean entityIdExists = false;
        try {
            String sql = "SELECT " + columnName + " FROM " + tableName;
            Statement statement = connectionDB.createStatement();
            ResultSet dbResponse = statement.executeQuery(sql);

            while (dbResponse.next()) {
                System.out.println(dbResponse.getObject(columnName));
                Object dbValue = dbResponse.getObject(columnName);
                if (dbValue != null && dbValue.equals(value)) {
                    entityIdExists = true;
                    break;
                }
            }

            dbResponse.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return entityIdExists;
    }

    public Integer checkValidUserDetailsLogin(String username, String password) {
        Integer user_id = 0;
        boolean userFound = false;
        try {
            String sql = "SELECT * FROM users"; //Prepare the SQL statement
            Statement statement = connectionDB.createStatement();
            ResultSet dbResponse = statement.executeQuery(sql); // Execute the SQL statement and get the results

            while (!userFound && dbResponse.next()) {
                System.out.println(dbResponse.getString("username"));
                System.out.println(dbResponse.getString("password"));
                System.out.println(dbResponse.getString("user_id"));
                if (dbResponse.getString("username").equals(username) && dbResponse.getString("password").equals(password)) {
                    user_id = dbResponse.getInt("user_id");
                    userFound = true;
                }
            }
            dbResponse.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            return user_id;
        }
    }
}
