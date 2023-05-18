package com.example.server.controllers;

import com.example.server.Database;
import com.example.server.ServerConstants;
import com.example.server.entities.*;
import com.example.server.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.*;

@RestController
@Component
public class RoomController {
    Database connectionDBInstance;
    Connection connectionDB;
    private final ControllerManager controllerManager;
    Map<Integer, Set<Integer>> roomParticipantsLive; // ROOM ID 1 is default room
    Map<Integer, List<Integer>> allRoomMembers;//room = userId
    Map<Integer, List<JoinRoomRequest>> joinRoomRequestMap; //mangerId - request
    Map<Integer, List<JoinRoomRequest>> handeledJoinRoomRequestMap; //userId - request

    @Autowired
    public RoomController(@Lazy ControllerManager controllerManager) {
        this.controllerManager = controllerManager;
        connectionDBInstance = Database.getInstance();
        connectionDB = connectionDBInstance.getConnection();
        roomParticipantsLive = new HashMap<>();
        roomParticipantsLive.put(ServerConstants.DEFAULT_ROOM, new HashSet<>());
        allRoomMembers = new HashMap<>();
        joinRoomRequestMap=new HashMap<>();

    }
    @PostMapping("/room")
    public ResponseEntity<Response> createRoom(@RequestParam String roomName, @RequestBody Room userRequestRoom) {
        boolean privacy = userRequestRoom.isPrivacy();
        int managerId = userRequestRoom.getManagerId();
        String description = userRequestRoom.getDescription();
        if (connectionDBInstance.isValueExist(ServerConstants.ROOMS_TABLE, "room_name", roomName)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RoomResponse(String.format(ServerConstants.ROOM_EXISTS, roomName), null, null));
        }
        if (!connectionDBInstance.isValueExist(ServerConstants.USERS_TABLE, "user_id", managerId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RoomResponse(String.format(ServerConstants.ROOM_EXISTS, roomName), null, null));
        }
        Room room = addNewRoomToDB(roomName, managerId, privacy, description);
        allRoomMembers.put(room.getRoomId(),new ArrayList<>());
        if (room != null) {
            roomParticipantsLive.put(room.getRoomId(),new HashSet<>());
            return ResponseEntity.status(HttpStatus.OK).body(new RoomResponse(String.format(ServerConstants.ROOM_CREATED_SUCCESSFULLY, roomName), room.getRoomId(), room));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RoomResponse(ServerConstants.UNEXPECTED_ERROR, null, room));
        }
    }
    @PostMapping("/getIntoRoom")
    public ResponseEntity<Response> getIntoRoom(@RequestParam Integer roomId, @RequestParam Integer userId) {
        try {
            removeUserFromRoom(userId);
            insertUserIdIntoRoom(userId, roomId);
            controllerManager.removeUserFromChatRoom(ServerConstants.DEFAULT_ROOM);
            controllerManager.addUserIntoChatRoom(userId,roomId);
            return ResponseEntity.status(HttpStatus.OK).body(new RoomResponse(String.format(ServerConstants.USER_CHANGED_ROOM_SUCCESSFULLY, userId, roomId), roomId, getRoomDetails(roomId)));
        } catch (Exception err) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RoomResponse(ServerConstants.UNEXPECTED_ERROR, 1, getRoomDetails(ServerConstants.DEFAULT_ROOM)));
        }
    }
    @PostMapping("/getOutFromRoom")
    public ResponseEntity<Response> getOutFromRoom(@RequestParam Integer userId) {
        try {
            removeUserFromRoom(userId);
            insertUserIdIntoRoom(userId, ServerConstants.DEFAULT_ROOM);
            controllerManager.removeUserFromChatRoom(userId);
            return ResponseEntity.status(HttpStatus.OK).body(new RoomResponse(String.format(ServerConstants.USER_CHANGED_ROOM_SUCCESSFULLY, userId, 1), 1, getRoomDetails(ServerConstants.DEFAULT_ROOM)));
        } catch (Exception err) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RoomResponse(ServerConstants.UNEXPECTED_ERROR, null, null));
        }
    }
    @GetMapping("/room/{roomId}")
    public ResponseEntity<Response> getRoomByRoomId(@PathVariable Integer roomId, @RequestParam Integer userId) {
        if (!connectionDBInstance.isValueExist(ServerConstants.ROOMS_TABLE, "room_id", roomId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RoomResponse(String.format(ServerConstants.ROOM_ID_NOT_EXISTS, roomId), null, null));
        }
        try {
            Room room = getRoomDetails(roomId);
            return ResponseEntity.status(HttpStatus.OK).body(new RoomResponse("Completed successfully", roomId, room));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RoomResponse(ServerConstants.UNEXPECTED_ERROR, roomId, null));
        }
    }
    @PostMapping("/deleteRoom/{roomId}")
    public ResponseEntity<Response> deleteRoom(@PathVariable("roomId") Integer roomId) { //delete all posters !!!!
    if (!connectionDBInstance.isValueExist(ServerConstants.ROOMS_TABLE, "room_id", roomId)) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RoomResponse(String.format(ServerConstants.ROOM_ID_NOT_EXISTS, roomId), null, null));
    }
    try {
        String sql = "DELETE FROM rooms WHERE room_id = ?";
        PreparedStatement pstmt = connectionDB.prepareStatement(sql);
        pstmt.setInt(1, roomId);
        int rowsDeleted = pstmt.executeUpdate();
        if (rowsDeleted == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RoomResponse(String.format(ServerConstants.UNEXPECTED_ERROR, roomId), roomId, null));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(new RoomResponse(String.format(ServerConstants.ROOM_DELETED_SUCCESSFULLY, roomId), roomId, null));
        }
    } catch (SQLException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RoomResponse(String.format(ServerConstants.UNEXPECTED_ERROR, roomId), roomId, null));
    }
}
    @GetMapping("/hall")
    public ResponseEntity<Response> getHall(@RequestParam Integer userId) {
        List<RoomStatus> roomStatuses = new ArrayList<>();
        try {
            PreparedStatement stmt = connectionDB.prepareStatement("SELECT * FROM rooms");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Integer roomId = rs.getInt("room_id");
                String roomName = rs.getString("room_name");
                String description = rs.getString("description");
                Boolean privacy = rs.getBoolean("privacy");
                Integer managerId = rs.getInt("manager_id");
                RoomStatus.RoomMemberStatus roomMembershipStatus = RoomStatus.RoomMemberStatus.NOT_A_MEMBER;
                JoinRoomRequest.RequestStatus joinRoomRequestStatus = null;
                if (isUserMemberInRoom(userId, roomId)) {
                    roomMembershipStatus = RoomStatus.RoomMemberStatus.MEMBER;
                } else {
                    joinRoomRequestStatus = getRequestStatus(userId, managerId, roomId);
                }
                roomStatuses.add(new RoomStatus(privacy, managerId, roomId, roomName, description, roomMembershipStatus, joinRoomRequestStatus));
            }
            return ResponseEntity.status(HttpStatus.OK).body(new HallResponse(roomStatuses, "Rooms status got successfully"));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new HallResponse(roomStatuses, "Rooms status maybe is not complete"));
        }
    }
    @GetMapping("/getJoinRoomRequests")
    public ResponseEntity<Response> getAllRequests(@RequestParam Integer managerId) {
        List<JoinRoomRequest> joinRoomRequests = joinRoomRequestMap.get(managerId);
        return ResponseEntity.status(HttpStatus.OK).body(new AllJoinReqResponse(joinRoomRequests, "Message data"));
    }
    @PostMapping("/joinRoom/{roomId}")
    public ResponseEntity<Response> askJoinRoom(@PathVariable("roomId") Integer roomId, @RequestParam Integer userId) {
        if (!connectionDBInstance.isValueExist(ServerConstants.ROOMS_TABLE, "room_id", roomId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RoomResponse(String.format(ServerConstants.ROOM_ID_NOT_EXISTS, roomId), null, null));
        }
        if (!connectionDBInstance.isValueExist(ServerConstants.USERS_TABLE, "user_id", userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RoomResponse(String.format(ServerConstants.USER_ID_NOT_EXISTS, roomId), null, null));
        }
        JoinRoomRequest joinRoomRequest = addNewJoinRoomRequest(userId,roomId);
        return ResponseEntity.status(HttpStatus.OK).body(new JoinRoomResponse(joinRoomRequest,ServerConstants.JOIN_ROOM_REQ_SENT_SUCCESSFULLY));
    }
    private void handleJoinRoomRequest(Integer userId, Integer roomId){
        for(List<JoinRoomRequest> joinRoomRequests : joinRoomRequestMap.values()){
            for(JoinRoomRequest request : joinRoomRequests){
                if(userId==request.getUserId() && roomId==request.getRoomId()){
                    switch (request.getRequestStatus()) {
                        case APPROVED:
                        case DECLINED:
                            List<JoinRoomRequest> joinRoomRequestList = handeledJoinRoomRequestMap.get(userId);
                            if (joinRoomRequestList == null) {
                                joinRoomRequestList = new ArrayList<>();
                            }
                            joinRoomRequestList.add(request);
                            handeledJoinRoomRequestMap.put(userId, joinRoomRequestList);
                            joinRoomRequests.remove(request);
                            break;
                    }
                }
            }
        }
    }
    private JoinRoomRequest.RequestStatus getRequestStatus(Integer userId, Integer managerId, Integer roomId){
        List<JoinRoomRequest> joinRoomRequestList = joinRoomRequestMap.get(managerId);
        if(joinRoomRequestList==null){
            joinRoomRequestList = new ArrayList<>();
        }
        for (JoinRoomRequest request : joinRoomRequestList){
            if (request.getRoomId() == roomId && request.getUserId()==userId){
                return request.getRequestStatus();
            }
        }
        return null;
    }
    private Boolean isUserMemberInRoom(Integer userId, Integer roomId){
        List<Integer> roomMembers = allRoomMembers.get(roomId);
        if (roomMembers==null){
            roomMembers = new ArrayList<>();
        }
        return roomMembers.contains(userId);
    }
    private JoinRoomRequest addNewJoinRoomRequest(Integer userId, Integer roomId) {
        Integer managerId = getRoomDetails(roomId).getManagerId();
        List<JoinRoomRequest> joinRoomRequestsByManager = joinRoomRequestMap.get(managerId);
        if (joinRoomRequestsByManager == null) {
            joinRoomRequestsByManager = new ArrayList<>();
            joinRoomRequestMap.put(managerId, joinRoomRequestsByManager);
        }
        JoinRoomRequest joinRoomRequest =new JoinRoomRequest(userId, roomId, JoinRoomRequest.RequestStatus.PENDING);
        joinRoomRequestsByManager.add(joinRoomRequest);
        return joinRoomRequest;
    }
//    @GetMapping ("/joinRoomRequests/{userId}")
//    public ResponseEntity<Response> askJoinRoom(@RequestParam Integer userId) {
//        if (!connectionDBInstance.isValueExist(ServerConstants.USERS_TABLE, "user_id", userId)) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RoomResponse(String.format(ServerConstants.USER_ID_NOT_EXISTS, roomId), null, null));
//        }
//        List<RoomParticipant> roomParticipantList = new ArrayList<>();
//        for (Map.Entry<Integer, List<RoomParticipant>> entry : statusPeopleInRoom.entrySet()) {
//            int roomId = entry.getKey();
//            List<RoomParticipant> participants = entry.getValue();
//            for (RoomParticipant participant : participants) {
//                if(participant.getManagerId() == userId){
//                    roomParticipantList.add(participant);
//                }
//                System.out.println("- User ID: " + participant.getUserId() + ", Approved: " + participant.isApproved());
//            }
//            System.out.println();
//        }
//        List<RoomParticipant> managerRoomRequests = joinRoomRequests.get(userId);
//        return ResponseEntity.status(HttpStatus.OK).body(new AllJoinReqResponse(managerRoomRequests, "All requests"));
//    }
//    public void addNewUserToRequestJoinRoomsSystem(Integer userId){
//        joinRoomRequests.put(userId,new ArrayList<>());
//    }
    public Room getRoomDetails(Integer roomId) {
        Boolean privacy;
        Integer managerId;
        String roomName;
        String description;
        try {
            PreparedStatement stmt = connectionDB.prepareStatement("SELECT * FROM rooms WHERE room_id = ?");
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                privacy = rs.getBoolean("privacy");
                managerId = rs.getInt("manager_id");
                roomName = rs.getString("room_name");
                description = rs.getString("description");
            } else {
                return null;
            }

            List<Poster> allPostersInRoom = controllerManager.getAllPostersInRoom(roomId);
            return new Room(privacy, managerId, roomId, roomName, allPostersInRoom,description);
        } catch (SQLException e) {
            return null;
        }
    }
    public Room addNewRoomToDB(String roomName, Integer managerId, boolean privacy, String description) {
        String insertSql = "INSERT INTO rooms (manager_id, room_name, privacy, description) VALUES (?,?,?,?)";
        Integer roomId = null;
        Room room = null;
        boolean roomCreated = false;
        try {
            PreparedStatement stmt = connectionDB.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, managerId);
            stmt.setString(2, roomName);
            stmt.setBoolean(3, privacy);
            stmt.setString(4, description);
            stmt.executeUpdate();
            roomCreated = true;

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                roomId = rs.getInt(1);
                room = new Room(privacy, managerId, roomId, roomName,null, description);
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
    public Integer findRoomIdByUserId(Integer userId) {
        for (Map.Entry<Integer, Set<Integer>> entry : roomParticipantsLive.entrySet()) {
            if (entry.getValue().contains(userId)) {
                return entry.getKey();
            }
        }
        return null;
    }
    public void insertUserIdIntoRoom(Integer userId, Integer roomId) {
        try {
            Set<Integer> room = roomParticipantsLive.get(roomId);
            if (room == null)
            {
                room = new HashSet<Integer>();
                roomParticipantsLive.put(roomId, room);
            }

            room.add(userId);

        } catch (Exception err) {
            throw err;
        }
    }
    public void removeUserFromRoom(Integer userId) {
        for (Map.Entry<Integer, Set<Integer>> room : roomParticipantsLive.entrySet()) {
            Set<Integer> participants = room.getValue();
            if (participants.contains(userId)) {
                participants.remove(userId);
                break;
            }
        }
    }
    public Set<Integer> getAllUsersInRoom(Integer roomId) {
        return roomParticipantsLive.get(roomId);
    }
    public boolean isUserOnline(Integer userId) {
        for (Set<Integer> room : roomParticipantsLive.values()) {
            if (room != null && room.contains(userId)) {
                return true; // User is online in at least one room
            }
        }
        return false; // User is not online in any room
    }
}
