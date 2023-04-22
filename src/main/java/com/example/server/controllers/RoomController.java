package com.example.server.controllers;

import com.example.server.Database;
import com.example.server.ServerConstants;
import com.example.server.entities.Room;
import com.example.server.response.ErrorRoomResponse;
import com.example.server.response.Response;
import com.example.server.response.SuccessRoomResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;

@RestController
public class RoomController {
    Database connectionDBInstance;
    Connection connectionDB;
    private String roomName;
    private Integer userId;

    public RoomController() {
        connectionDBInstance = Database.getInstance();
        connectionDB = connectionDBInstance.getConnection();
    }

    @PostMapping("/room")
    public ResponseEntity<Response> createRoom(@RequestParam String roomName, @RequestParam Integer userId, @RequestBody Room userRequestRoom) {
        int maxCapacity = userRequestRoom.getMaxCapacity();
        boolean privacy = userRequestRoom.isPrivacy();
        if (connectionDBInstance.checkRoomNameExist(roomName)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorRoomResponse(String.format(ServerConstants.ROOM_EXISTS, roomName), userId, null));
        }
        Integer roomId = updateRoomTablesDB(roomName, userId, privacy, maxCapacity);
        if (roomId != null) {
            return ResponseEntity.status(HttpStatus.OK).body(new SuccessRoomResponse(String.format(ServerConstants.ROOM_CREATED_SUCCESSFULLY, roomName), userId, roomId, maxCapacity, privacy));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorRoomResponse(ServerConstants.UNEXPECTED_ERROR, userId, null));
        }
    }

    public Integer updateRoomTablesDB (String roomName, Integer managerId, boolean privacy, int maxCapacity) {
        Integer roomId = addRoomDetailsToRoomsTable(roomName, managerId,privacy,maxCapacity);
        if(roomId!=0){
            if (createNewRoomTable(roomId)) {
                return roomId;
            }
        }else {
            //TODO problem insert new room id
        }
        return 0;
    }

    public boolean createNewRoomTable(Integer roomId) {
        boolean roomCreated = false;
        String tableName = "room_id_" + roomId;
        String sql = "CREATE TABLE " + tableName + " ("
                + "users_id INT,"
                + "posters_id INT,"
                + "FOREIGN KEY (users_id) REFERENCES users(user_id),"
                + "FOREIGN KEY (posters_id) REFERENCES posters(posters_id)"
                + ")";
        try {
            Statement stmt = connectionDB.createStatement();
            stmt.executeUpdate(sql);
            roomCreated = true;
        } catch (SQLException e) {
            //TODO failed create room table: delete room ?
        } finally {
            return roomCreated;
        }
    }

    public Integer addRoomDetailsToRoomsTable(String roomName, Integer managerId,boolean privacy, int maxCapacity) {
        String insertSql = "INSERT INTO rooms (manager_id, room_name, privacy, max_capacity) VALUES (?,?,?,?)";
        Integer room_id = null;
        boolean roomCreated = false;
        try {
            PreparedStatement stmt = connectionDB.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, managerId);
            stmt.setString(2, roomName);
            stmt.setBoolean(3, privacy);
            stmt.setInt(4, maxCapacity);
            stmt.executeUpdate();
            roomCreated = true;

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                room_id = rs.getInt(1);
            }
            stmt.close();
        } catch (SQLException e) {
            if (roomCreated) {
                //TODO handle
            }
        } finally {
            return room_id;
        }
    }

    @PostMapping("/rooms/{roomId}/users")
    public ResponseEntity<Response> addUserToRoom(@PathVariable Integer roomId, @org.springframework.web.bind.annotation.RequestBody RequestBody userRequest) {
//        Integer userId = userRequest.getUserId();
//
//        if (!connectionDBInstance.checkUserIdExist(userId)) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RoomResponse(String.format(ServerConstants.USER_ID_NOT_EXISTS, userId), userId, null));
//        }
//
//        if (!connectionDBInstance.checkRoomIdExist(roomId)) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RoomResponse(String.format(ServerConstants.ROOM_ID_NOT_EXISTS, roomId), userId, null));
//        }
    return null;
        // Rest of the code here
    }


//    @PostMapping("/rooms/{roomId}/users")
//    public String processRequest(@QueryParam("param1") String param1, @QueryParam("param2") int param2, String rawData) {
//        // Parse the raw JSON data into a JsonNode object using Jackson
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode jsonNode = objectMapper.readTree(rawData);
//
//        // Extract parameters from the JSON data
//        String jsonParam1 = jsonNode.get("jsonParam1").asText();
//        int jsonParam2 = jsonNode.get("jsonParam2").asInt();
//
//        // Process the request using all parameters
//        return "Request processed successfully";
//    }

//    private boolean addUserToRoomDB(Integer roomId, Integer userId){
//        // SQL query to insert a new row
//        String tableName = "room_id_"+roomId;
//        String sql = "INSERT INTO "+tableName+" (column1, column2, column3) VALUES (?, ?, ?)";
//
//        try (Connection conn = DriverManager.getConnection(url, username, password);
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            // Set the values of the columns for the new row
//            pstmt.setString(1, "value1");
//            pstmt.setInt(2, 123);
//            pstmt.setBoolean(3, true);
//
//            // Execute the SQL query
//            int rowsInserted = pstmt.executeUpdate();
//
//            if (rowsInserted > 0) {
//                System.out.println("A new row has been added to the table.");
//            } else {
//                System.out.println("No rows have been added to the table.");
//            }
//        } catch (SQLException e) {
//            System.out.println("Error executing SQL query: " + e.getMessage());
//        }
//    }

}
