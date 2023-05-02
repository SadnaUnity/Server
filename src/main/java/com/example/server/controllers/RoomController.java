package com.example.server.controllers;

import com.example.server.Database;
import com.example.server.ServerConstants;
import com.example.server.entities.Room;
import com.example.server.response.AvatarResponse;
import com.example.server.response.Response;
import com.example.server.response.RoomResponse;
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
    public ResponseEntity<Response> createRoom(@RequestParam String roomName,  @RequestBody Room userRequestRoom) {
        int maxCapacity = userRequestRoom.getMaxCapacity();
        boolean privacy = userRequestRoom.isPrivacy();
        int managerId = userRequestRoom.getManagerId();
        if (connectionDBInstance.isValueExist(ServerConstants.ROOMS_TABLE,"room_name",roomName)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RoomResponse(String.format(ServerConstants.ROOM_EXISTS, roomName), null, null));
        }
        if (!connectionDBInstance.isValueExist(ServerConstants.USERS_TABLE,"user_id",managerId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RoomResponse(String.format(ServerConstants.ROOM_EXISTS, roomName), null, null));
        }
        Room room = addNewRoomToDB(roomName, managerId, privacy, maxCapacity);
        if (room != null) {
            return ResponseEntity.status(HttpStatus.OK).body(new RoomResponse(String.format(ServerConstants.ROOM_CREATED_SUCCESSFULLY, roomName), room.getRoomId(), room));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RoomResponse(ServerConstants.UNEXPECTED_ERROR, null, room));
        }
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<Response> getRoomByRoomId(@PathVariable Integer roomId) {
        if (!connectionDBInstance.isValueExist(ServerConstants.ROOMS_TABLE,"room_id",roomId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RoomResponse(String.format(ServerConstants.ROOM_ID_NOT_EXISTS, roomId), null, null));
        }
        try {
            Room room = getRoom(roomId);
            return ResponseEntity.ok().body(new RoomResponse("Completed successfully", roomId, room));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AvatarResponse(ServerConstants.UNEXPECTED_ERROR, userId, null));
        }
    }

    public Room getRoom(Integer roomId) {
        try {
            PreparedStatement stmt = connectionDB.prepareStatement("SELECT * FROM rooms WHERE room_id = ?");
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Boolean privacy = rs.getBoolean("privacy");
                Integer maxCapacity = (rs.getInt("max_capacity"));
                Integer managerId = rs.getInt("manager_id");
                String roomName = rs.getString("room_name");
                return new Room(privacy, managerId, maxCapacity, roomId, roomName);
            } else {
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public Room addNewRoomToDB(String roomName, Integer managerId, boolean privacy, int maxCapacity){
        String insertSql = "INSERT INTO rooms (manager_id, room_name, privacy, max_capacity) VALUES (?,?,?,?)";
        Integer roomId = null;
        Room room = null;
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
                roomId = rs.getInt(1);
                room = new Room(privacy,managerId,maxCapacity,roomId,roomName);
            }
            stmt.close();
        } catch (SQLException e) {
            if (roomCreated) {
                //TODO handle
            }
        } finally {
            return room;
        }
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
