package com.example.server.controllers;

import com.example.server.Database;
import com.example.server.ServerConstants;
import com.example.server.entities.*;
import com.example.server.response.*;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.google.cloud.storage.Blob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static com.example.server.response.Response.badRequestResponse;

@RestController
@Component
public class RoomController {
    Database connectionDBInstance;
    Connection connectionDB;
    Storage gcpStorage;

    private final ControllerManager controllerManager;
    Map<Integer, Set<Integer>> roomParticipantsLive; // ROOM ID 1 is default room
    Map<Integer, List<Integer>> allRoomMembers;//room = userId - for each room - who are the approved user
    Map<Integer, List<JoinRoomRequest>> joinRoomRequestMap; //mangerId - request - for each manager id - all request that are pending
    Map<Integer, List<JoinRoomRequest>> completedRequestsMapByUser; //userId - request - for each user id - all requests that approved or declined

    @Autowired
    public RoomController(@Lazy ControllerManager controllerManager) {
        try {
            this.controllerManager = controllerManager;
            connectionDBInstance = Database.getInstance();
            connectionDB = connectionDBInstance.getConnection();
            roomParticipantsLive = new HashMap<>();
            roomParticipantsLive.put(ServerConstants.DEFAULT_ROOM, new HashSet<>());
            allRoomMembers = new HashMap<>();
            joinRoomRequestMap = new HashMap<>();
            completedRequestsMapByUser = new HashMap<>();
            justForUs();
            gcpStorage = StorageOptions.newBuilder().setProjectId(ServerConstants.PROJECT_ID).setCredentials(GoogleCredentials.fromStream(getClass().getResourceAsStream("/credentialsData.json"))).build().getService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void justForUs() {
        try {
            PreparedStatement stmt = connectionDB.prepareStatement("SELECT * FROM rooms");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Integer roomId = rs.getInt("room_id");
                Integer managerId = rs.getInt("manager_id");
                allRoomMembers.put(roomId, new ArrayList<>());
                allRoomMembers.get(roomId).add(managerId);
                //roomParticipantsLive.put(roomId, new HashSet<>());
                //roomParticipantsLive.get(roomId).add(managerId);
            }
        } catch (SQLException e) {
        }
    }
    public void addAllManagerIdIntoRoom() { //in case of server dead
        try {
            PreparedStatement stmt = connectionDB.prepareStatement("SELECT * FROM rooms");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Integer roomId = rs.getInt("room_id");
                Integer managerId = rs.getInt("manager_id");
                allRoomMembers.put(roomId, new ArrayList<>());
                allRoomMembers.get(roomId).add(managerId);
            }
        } catch (SQLException e) {
        }
    }
//    @PostMapping("/room")
//    public ResponseEntity<Response> createRoom(@RequestParam String roomName, @RequestParam String description, @RequestParam Integer managerId, @RequestParam String background, @RequestPart MultipartFile file, @RequestParam boolean privacy) {
//        System.out.println(roomName);
//        System.out.println(description);
//        System.out.println(managerId);
//        System.out.println(background);
//        System.out.println(privacy);
//        System.out.println(file);
//        if (file.isEmpty()) {
//            return badRequestResponse(ServerConstants.IMAGE_EMPTY);
//        }
//        if (file.getSize() > 10485760) {
//            return badRequestResponse(ServerConstants.FILE_TOO_BIG);
//        }
//
//        if (connectionDBInstance.isValueExist(ServerConstants.ROOMS_TABLE, "room_name", roomName)) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RoomResponse(String.format(ServerConstants.ROOM_EXISTS, roomName), null, null));
//        }
//        if (!connectionDBInstance.isValueExist(ServerConstants.USERS_TABLE, "user_id", managerId)) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RoomResponse(String.format(ServerConstants.ROOM_EXISTS, roomName), null, null));
//        }
//        String roomImageId = UUID.randomUUID().toString();
//        BlobId blobId = BlobId.of(ServerConstants.BUCKET_NAME, roomImageId);
//        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
//        byte[] fileData ;
//        String url;
//        try {
//            fileData = file.getBytes();
//            Blob blob = gcpStorage.create(blobInfo, fileData);
//            url = blob.getMediaLink();
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RoomResponse(ServerConstants.FILE_UPLOAD_FAILED, null, null));
//        }
//        Room room = addNewRoomToDB(roomName, Integer.valueOf(managerId), privacy, description, Room.Background.valueOf(background), url);
//        int roomId = room.getRoomId();
//        allRoomMembers.put(roomId, new ArrayList<>());
//        allRoomMembers.get(room.getRoomId()).add(room.getManagerId());
//        if (room != null) {
//            roomParticipantsLive.put(room.getRoomId(), new HashSet<>());
//            return ResponseEntity.status(HttpStatus.OK).body(new RoomResponse(String.format(ServerConstants.ROOM_CREATED_SUCCESSFULLY, roomName), room.getRoomId(), room));
//        } else {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RoomResponse(ServerConstants.UNEXPECTED_ERROR, null, room));
//        }
//    }
    @PostMapping("/roomImage/{roomId}")
    public ResponseEntity<Response> updateRoomImage(@PathVariable Integer roomId, @RequestParam Integer userId, @RequestPart MultipartFile file) {
        if (!connectionDBInstance.isValueExist(ServerConstants.ROOMS_TABLE, "room_id", roomId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RoomResponse(String.format(ServerConstants.ROOM_ID_NOT_EXISTS, roomId), null, null));
        }
        if (!isUserRoomMember(userId, roomId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RoomResponse(String.format(ServerConstants.USER_NOT_A_ROOM_MEMBER, userId), null, null));
        }
        if (file.isEmpty()) {
            return badRequestResponse(ServerConstants.IMAGE_EMPTY);
        }
        if (file.getSize() > 10485760) {
            return badRequestResponse(ServerConstants.FILE_TOO_BIG);
        }
        String roomImageId = UUID.randomUUID().toString();
        BlobId blobId = BlobId.of(ServerConstants.BUCKET_NAME, roomImageId);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
        byte[] fileData;
        String url;
        try {
            fileData = file.getBytes();
            Blob blob = gcpStorage.create(blobInfo, fileData);
            url = blob.getMediaLink();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RoomResponse(ServerConstants.FILE_UPLOAD_FAILED, null, null));
        }
        if (updateRoomURL(roomId, url)) {
            return ResponseEntity.status(HttpStatus.OK).body(new RoomResponse(String.format(ServerConstants.ROOM_UPDATED_SUCCESSFULLY), roomId, getRoomDetails(roomId)));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RoomResponse(ServerConstants.FILE_UPLOAD_FAILED, null, null));
        }
    }

    public boolean updateRoomURL(int roomId, String newUrl) {
        boolean urlUpdated = false;
        try {
            // Prepare the update query
            String updateQuery = "UPDATE rooms SET url = ? WHERE room_id = ?";
            PreparedStatement preparedStatement = connectionDB.prepareStatement(updateQuery);

            // Set the parameters for the query
            preparedStatement.setString(1, newUrl);
            preparedStatement.setInt(2, roomId);

            // Execute the update query
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                urlUpdated = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return urlUpdated;
        }
    }
    @PostMapping("/room")
    public ResponseEntity<Response> createRoom(@RequestParam String roomName, @RequestBody Room userRequestRoom) {
        boolean privacy = userRequestRoom.isPrivacy();
        int managerId = userRequestRoom.getManagerId();
        String description = userRequestRoom.getDescription();
        Room.Background background = userRequestRoom.getBackground();
        if (connectionDBInstance.isValueExist(ServerConstants.ROOMS_TABLE, "room_name", roomName)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RoomResponse(String.format(ServerConstants.ROOM_EXISTS, roomName), null, null));
        }
        if (!connectionDBInstance.isValueExist(ServerConstants.USERS_TABLE, "user_id", managerId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RoomResponse(String.format(ServerConstants.ROOM_EXISTS, roomName), null, null));
        }
        Room room = addNewRoomToDB(roomName, managerId, privacy, description, background, null);
        int roomId = room.getRoomId();
        allRoomMembers.put(roomId, new ArrayList<>());
        allRoomMembers.get(room.getRoomId()).add(room.getManagerId());
        if (room != null) {
            roomParticipantsLive.put(room.getRoomId(), new HashSet<>());
            return ResponseEntity.status(HttpStatus.OK).body(new RoomResponse(String.format(ServerConstants.ROOM_CREATED_SUCCESSFULLY, roomName), room.getRoomId(), room));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RoomResponse(ServerConstants.UNEXPECTED_ERROR, null, room));
        }
    }

    @PostMapping("/getIntoRoom")
    public ResponseEntity<Response> getIntoRoom(@RequestParam Integer roomId, @RequestParam Integer userId) {
        try {
            if (!isUserRoomMember(userId, roomId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RoomResponse(String.format(ServerConstants.USER_NOT_A_ROOM_MEMBER, userId), null, null));
            }
            removeUserFromRoom(userId);
            insertUserIdIntoRoom(userId, roomId);
//            controllerManager.removeUserFromChatRoom(ServerConstants.DEFAULT_ROOM);
//            controllerManager.addUserIntoChatRoom(userId, roomId);
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
//            controllerManager.removeUserFromChatRoom(userId);
            return ResponseEntity.status(HttpStatus.OK).body(new HallResponse(getHallDetails(userId), "hall data"));
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RoomResponse(String.format(ServerConstants.ROOM_ID_NOT_EXISTS, roomId), roomId, null));
        }
        if (isAllowedDeleteRoom(roomId,managerId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new RoomResponse(String.format(ServerConstants.NO_PERMISSION_DELETE_ROOM, managerId), roomId, null));
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
        JoinRoomRequest joinRoomRequest = addNewJoinRoomRequest(userId, roomId);
        return ResponseEntity.status(HttpStatus.OK).body(new JoinRoomResponse(joinRoomRequest, ServerConstants.JOIN_ROOM_REQ_SENT_SUCCESSFULLY));
    }

    @PostMapping("/handlePendingJoinRequests")
    public ResponseEntity<Response> sendJoinRoomRequests(@RequestBody List<JoinRoomRequest> joinRoomRequestsToHandle, @RequestParam Integer managerId) {
        List<JoinRoomRequest> handledSuccessfullyRequests = new ArrayList<>();
        List<JoinRoomRequest> notHandledRequests = new ArrayList<>();
        String msg = "Request handled";
        try {
            for (JoinRoomRequest requestToHandle : joinRoomRequestsToHandle) {
                boolean handleRequest = handleRequest(requestToHandle, managerId);
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
        if(userCompletedRequests==null || userCompletedRequests.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AllJoinReqResponse(null, "user id: " + userId + " does not have completed request..."));
        }
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

    private boolean isAllowedDeleteRoom(Integer roomId, Integer managerId) {
        return getRoomDetails(roomId).getManagerId() == managerId;
    }

    private boolean isUserRoomMember(Integer userId, Integer roomId) {
        return allRoomMembers.get(roomId).contains(userId);
    }

    private boolean handleRequest(JoinRoomRequest requestToHandle, Integer managerId) {
        boolean requestHandled = false;
        try {
            Integer userId = requestToHandle.getUserId();
            Integer roomId = requestToHandle.getRoomId();
            List<JoinRoomRequest> joinRoomRequestsForManager = joinRoomRequestMap.get(managerId);
            if (joinRoomRequestsForManager == null || joinRoomRequestsForManager.isEmpty()) {
                return false;
            }
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
                if (requestToHandle.getRequestStatus() == JoinRoomRequest.RequestStatus.APPROVED) {
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

    private List<RoomStatus> getHallDetails(Integer userId) {
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
                String url = rs.getString("url");
                RoomStatus.RoomMemberStatus roomMembershipStatus = RoomStatus.RoomMemberStatus.NOT_A_MEMBER;
                JoinRoomRequest.RequestStatus joinRoomRequestStatus = null;
                if (isUserRoomMember(userId, roomId)) {
                    roomMembershipStatus = RoomStatus.RoomMemberStatus.MEMBER;
                } else {
                    joinRoomRequestStatus = getRequestStatus(userId, managerId, roomId);
                }
                roomStatuses.add(new RoomStatus(privacy, managerId, roomId, roomName, description, roomMembershipStatus, joinRoomRequestStatus,url));
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

    private JoinRoomRequest addNewJoinRoomRequest(Integer userId, Integer roomId) {
        Integer managerId = getRoomDetails(roomId).getManagerId();
        List<JoinRoomRequest> joinRoomRequestsByManager = joinRoomRequestMap.get(managerId);
        if (joinRoomRequestsByManager == null) {
            joinRoomRequestsByManager = new ArrayList<>();
            joinRoomRequestMap.put(managerId, joinRoomRequestsByManager);
        }
        JoinRoomRequest joinRoomRequest = new JoinRoomRequest(userId, roomId, JoinRoomRequest.RequestStatus.PENDING,controllerManager.getUserName(userId));
        joinRoomRequestsByManager.add(joinRoomRequest);
        return joinRoomRequest;
    }

    public Room getRoomDetails(Integer roomId) {
        Boolean privacy;
        Integer managerId;
        String roomName;
        String description;
        Room.Background background;
        String url;
        try {
            PreparedStatement stmt = connectionDB.prepareStatement("SELECT * FROM rooms WHERE room_id = ?");
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                privacy = rs.getBoolean("privacy");
                managerId = rs.getInt("manager_id");
                roomName = rs.getString("room_name");
                description = rs.getString("description");
                background = Room.Background.valueOf(rs.getString("background"));
                url = rs.getString("url");

            } else {
                return null;
            }

            List<Poster> allPostersInRoom = controllerManager.getAllPostersInRoom(roomId);
            return new Room(privacy, managerId, roomId, roomName, allPostersInRoom, description, background, url);
        } catch (SQLException e) {
            return null;
        }
    }

    public Room addNewRoomToDB(String roomName, Integer managerId, boolean privacy, String description, Room.Background background, String url) {
        String insertSql = "INSERT INTO rooms (manager_id, room_name, privacy, description, background, url) VALUES (?,?,?,?,?,?)";
        Integer roomId = null;
        Room room = null;
        boolean roomCreated = false;
        try {
            PreparedStatement stmt = connectionDB.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, managerId);
            stmt.setString(2, roomName);
            stmt.setBoolean(3, privacy);
            stmt.setString(4, description);
            stmt.setString(5, String.valueOf(background));
            stmt.setString(6, url);
            stmt.executeUpdate();
            roomCreated = true;

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                roomId = rs.getInt(1);
                room = new Room(privacy, managerId, roomId, roomName, null, description, background,url);
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
            if (room == null) {
                room = new HashSet<>();
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

    public Integer getRoomManager(Integer roomId) {
        return getRoomDetails(roomId).getManagerId();
    }
}
