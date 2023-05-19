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

//todo : delete posters - only admin or user that upload the poster
@RestController
@Component
public class RoomController {
    Database connectionDBInstance;
    Connection connectionDB;
    private final ControllerManager controllerManager;
    Map<Integer, Set<Integer>> roomParticipantsLive; // ROOM ID 1 is default room
    Map<Integer, List<Integer>> allRoomMembers;//room = userId
    Map<Integer, List<JoinRoomRequest>> joinRoomRequestMap; //mangerId - request
    Map<Integer, List<JoinRoomRequest>> completedRequestsMapByUser; //userId - request

    @Autowired
    public RoomController(@Lazy ControllerManager controllerManager) {
        this.controllerManager = controllerManager;
        connectionDBInstance = Database.getInstance();
        connectionDB = connectionDBInstance.getConnection();
        roomParticipantsLive = new HashMap<>();
        roomParticipantsLive.put(ServerConstants.DEFAULT_ROOM, new HashSet<>());
        allRoomMembers = new HashMap<>();
        joinRoomRequestMap=new HashMap<>();
        completedRequestsMapByUser =new HashMap<>();
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
            if(!isUserRoomMember(userId,roomId)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RoomResponse(String.format(ServerConstants.USER_NOT_A_ROOM_MEMBER, userId), null, null));
            }
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
            return ResponseEntity.status(HttpStatus.OK).body(new HallResponse(getHallDetails(userId),"hall data"));
        } catch (Exception err) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new HallResponse(getHallDetails(userId), ServerConstants.UNEXPECTED_ERROR));
        }
    }
    @GetMapping("/room/{roomId}")
    public ResponseEntity<Response> retrieveRoom(@PathVariable Integer roomId, @RequestParam Integer userId) {
        if (!connectionDBInstance.isValueExist(ServerConstants.ROOMS_TABLE, "room_id", roomId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RoomResponse(String.format(ServerConstants.ROOM_ID_NOT_EXISTS, roomId), null, null));
        }
        if (!isUserRoomMember(userId, roomId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RoomResponse(String.format(ServerConstants.USER_NOT_A_ROOM_MEMBER, userId), null, null));
        }
        try {
            Room room = getRoomDetails(roomId);
            return ResponseEntity.status(HttpStatus.OK).body(new RoomResponse("Completed successfully", roomId, room));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RoomResponse(ServerConstants.UNEXPECTED_ERROR, roomId, null));
        }
    }
    @PostMapping("/deleteRoom/{roomId}")
    public ResponseEntity<Response> deleteRoom(@PathVariable("roomId") Integer roomId, @RequestParam Integer managerId) { //delete all posters !!!!
    if (!connectionDBInstance.isValueExist(ServerConstants.ROOMS_TABLE, "room_id", roomId)) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RoomResponse(String.format(ServerConstants.ROOM_ID_NOT_EXISTS, roomId), null, null));
    }
    if(getRoomDetails(roomId).getManagerId() !=managerId){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RoomResponse(String.format(ServerConstants.USER_NOT_A_ROOM_MANAGER, managerId), null, null));
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
    public ResponseEntity<Response> getHallEndpoint(@RequestParam Integer userId) {
        List<RoomStatus> roomStatuses = getHallDetails(userId);
        return ResponseEntity.status(HttpStatus.OK).body(new HallResponse(roomStatuses, "Rooms status got successfully"));
    }
    @GetMapping("/waitingJoinRoomRequests")
    public ResponseEntity<Response> waitingJoinRoomRequests(@RequestParam Integer managerId) {
        List<JoinRoomRequest> joinRoomRequests = joinRoomRequestMap.get(managerId);
        return ResponseEntity.status(HttpStatus.OK).body(new AllJoinReqResponse(joinRoomRequests, "Message data"));
    }
    @GetMapping("/completedRequests")
    public ResponseEntity<Response> completedRequests(@RequestParam Integer userId) {
        List<JoinRoomRequest> joinRoomRequests = completedRequestsMapByUser.get(userId);
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
    @PostMapping("/handlePendingJoinRequests")
    public ResponseEntity<Response> sendJoinRoomRequests(@RequestBody List<JoinRoomRequest> joinRoomRequestsToHandle, @RequestParam Integer managerId) {
        List<JoinRoomRequest> handledSuccessfullyRequests = new ArrayList<>();
        List<JoinRoomRequest> notHandledRequests = new ArrayList<>();
        String msg = "Request handled";
        try {
            for (JoinRoomRequest requestToHandle : joinRoomRequestsToHandle) {
                boolean handleRequest = handleRequest(requestToHandle,managerId);
                if (handleRequest) {
                    handledSuccessfullyRequests.add(requestToHandle);
                } else {
                    notHandledRequests.add(requestToHandle);
                }
            }
        } catch (Exception err) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AllJoinReqResponse(handledSuccessfullyRequests, "Internal error. some of the requests failed"));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new AllJoinReqResponse(handledSuccessfullyRequests, msg, notHandledRequests));
    }
    @PostMapping("/approveRequest")
    public ResponseEntity<Response> approveRequest(@RequestBody List<JoinRoomRequest> approvedRequests, @RequestParam Integer userId) {
        List<JoinRoomRequest> handledSuccessfullyRequests = new ArrayList<>();
        List<JoinRoomRequest> notHandledRequests = new ArrayList<>();
        String msg = "Request handled";
        List<JoinRoomRequest> userCompletedRequests = completedRequestsMapByUser.get(userId);
        try {
            for (JoinRoomRequest requestToApprove : approvedRequests) {
                Optional<JoinRoomRequest> optionalMatchingRequest = userCompletedRequests.stream()
                        .filter(request -> request.getUserId().equals(userId) && request.getRoomId().equals(requestToApprove.getRoomId()) && request.getRequestStatus().equals(requestToApprove.getRequestStatus()))
                        .findFirst();
                JoinRoomRequest matchingRequest = optionalMatchingRequest.get();
                if (optionalMatchingRequest.isPresent()) {
                    userCompletedRequests.remove(matchingRequest);
                    handledSuccessfullyRequests.add(matchingRequest);
                } else {
                    notHandledRequests.add(matchingRequest);
                }
            }
        } catch (Exception err) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AllJoinReqResponse(handledSuccessfullyRequests, "Internal error. some of the requests failed"));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new AllJoinReqResponse(handledSuccessfullyRequests, msg, notHandledRequests));
    }

    private boolean isUserRoomMember(Integer userId, Integer roomId){
        return allRoomMembers.get(roomId).contains(userId);
    }
    private boolean handleRequest(JoinRoomRequest requestToHandle, Integer managerId) {
        boolean requestHandled = false;
        try {
            Integer userId = requestToHandle.getUserId();
            Integer roomId = requestToHandle.getRoomId();
            List<JoinRoomRequest> joinRoomRequestsForManager = joinRoomRequestMap.get(managerId);

            Optional<JoinRoomRequest> optionalMatchingRequest = joinRoomRequestsForManager.stream()
                    .filter(request -> request.getUserId().equals(userId) && request.getRoomId().equals(roomId))
                    .findFirst();
            if (optionalMatchingRequest.isPresent()) {
                List<JoinRoomRequest> userHandledJoinRoomRequests = completedRequestsMapByUser.get(userId);
                if (userHandledJoinRoomRequests == null) {
                    userHandledJoinRoomRequests = new ArrayList<>();
                    completedRequestsMapByUser.put(userId, userHandledJoinRoomRequests);
                }
                userHandledJoinRoomRequests.add(requestToHandle);
                joinRoomRequestsForManager.remove(optionalMatchingRequest.get());
                if(requestToHandle.getRequestStatus()== JoinRoomRequest.RequestStatus.APPROVED){
                    List<Integer> roomMembers = allRoomMembers.get(roomId);
                    if (roomMembers == null) {
                        roomMembers = new ArrayList<>();
                        allRoomMembers.put(roomId, roomMembers);
                    }
                    roomMembers.add(userId);
                }
                requestHandled = true;
            }
        } catch (Exception err) {
        } finally {
            return requestHandled;
        }
    }
    private List<RoomStatus> getHallDetails(Integer userId){
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
        } catch (SQLException e) {
        } finally {
            return roomStatuses;
        }
    }
    private JoinRoomRequest.RequestStatus getRequestStatus(Integer userId, Integer managerId, Integer roomId) {
        List<JoinRoomRequest> joinRoomRequestList = joinRoomRequestMap.get(managerId);
        if (joinRoomRequestList != null) {
            for (JoinRoomRequest request : joinRoomRequestList) {
                if (request.getRoomId() == roomId && request.getUserId() == userId) {
                    return request.getRequestStatus();
                }
            }
        }
        joinRoomRequestList = completedRequestsMapByUser.get(userId);
        if (joinRoomRequestList != null) {
            for (JoinRoomRequest request : joinRoomRequestList) {
                if (request.getRoomId() == roomId) {
                    return request.getRequestStatus();
                }
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
