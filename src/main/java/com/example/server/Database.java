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

    public boolean checkRoomExist(String room_id){
        boolean roomExist = false;
        try {
            String sql = "SELECT room_id FROM rooms"; //Prepare the SQL statement
            Statement statement = connectionDB.createStatement();
            ResultSet dbResponse = statement.executeQuery(sql); // Execute the SQL statement and get the results

            while (dbResponse.next()) {
                if (dbResponse.getString("room_id").equals(room_id)) {
                    roomExist = true;
                    break;
                }
            }

            dbResponse.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return roomExist;
    }

    public boolean checkRoomNameExist(String room_name) {
        boolean posterExist = false;
        try {
            String sql = "SELECT room_name FROM rooms"; //Prepare the SQL statement
            Statement statement = connectionDB.createStatement();
            ResultSet dbResponse = statement.executeQuery(sql); // Execute the SQL statement and get the results

            while (dbResponse.next()) {
                System.out.println(dbResponse.getString("room_name"));
                if (dbResponse.getString("room_name").equals(room_name)) {
                    posterExist = true;
                    break;
                }
            }

            dbResponse.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return posterExist;
    }

    public boolean checkPosterNameExist(String posterName) {
        boolean posterExist = false;
        try {
            String sql = "SELECT poster_name FROM posters"; //Prepare the SQL statement
            Statement statement = connectionDB.createStatement();
            ResultSet dbResponse = statement.executeQuery(sql); // Execute the SQL statement and get the results

            while (dbResponse.next()) {
                System.out.println(dbResponse.getString("poster_name"));
                if (dbResponse.getString("poster_name").equals(posterName)) {
                    posterExist = true;
                    break;
                }
            }

            dbResponse.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return posterExist;
    }

    public boolean checkUserExist(String username) {
        boolean userExist = false;
        try {
            String sql = "SELECT username FROM users"; //Prepare the SQL statement
            Statement statement = connectionDB.createStatement();
            ResultSet dbResponse = statement.executeQuery(sql); // Execute the SQL statement and get the results

            while (dbResponse.next()) {
                if (dbResponse.getString("username").equals(username)) {
                    userExist = true;
                    break;
                }
            }

            dbResponse.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            return userExist;
        }
    }

    public void createTables(){
        //ROOM
        String createRoomsTableSQL = "CREATE TABLE rooms (" +
                "room_id INT PRIMARY KEY AUTO_INCREMENT," +
                "manager_id INT," +
                "FOREIGN KEY (manager_id) REFERENCES users(user_id)" +
                ")";
        //POSTERS
        String createPostersTableSQL = "CREATE TABLE posters (" +
                "poster_id INT PRIMARY KEY AUTO_INCREMENT," +
                "user_id INT," +
                "image BLOB," +
                "room_id INT," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id)," +
                "FOREIGN KEY (room_id) REFERENCES rooms(room_id)" +
                ")";
        //users
//        CREATE TABLE users (
//                user_id INT AUTO_INCREMENT PRIMARY KEY,
//                username VARCHAR(50),
//                password VARCHAR(50)
//        );
    }
    private void deleteFromTable() {
//        String createTable = "DELETE FROM global_ids WHERE entity_name IN ('next_room_id', 'next_poster_id','next_user_id')";
        String createTable = "DELETE FROM users WHERE username IN ('mai', 'next_poster_id','next_user_id')";
        Statement stmt = null;


        try {
            stmt = connectionDB.prepareStatement(createTable);
            stmt.executeUpdate(createTable);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}