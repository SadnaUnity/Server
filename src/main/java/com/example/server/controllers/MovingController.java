package com.example.server.controllers;

import com.example.server.ServerConstants;
import com.example.server.entities.Avatar;
import com.example.server.entities.Position;
import com.example.server.response.PositionsResponse;
import com.example.server.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
public class MovingController {
    Map<Integer, Position> positions;
    RoomController roomController;
    AvatarController avatarController;
    public MovingController(RoomController roomController, AvatarController avatarController) {
        positions = new HashMap<>();
        positions.put(1,new Position(1,2,3));
        positions.put(3,new Position(3,2,3));
        this.roomController = roomController;
        this.avatarController = avatarController;
    }
    @PostMapping("/updatePosition")
    public ResponseEntity<String> updatePosition(@RequestBody Position posData) {
        positions.put(posData.getId(), posData);
        return ResponseEntity.status(HttpStatus.OK).body(ServerConstants.POSITION_UPDATED_SUCCESSFULLY);

    }
    @GetMapping("/getPositions")
    public ResponseEntity<Response> getPositions(@RequestParam Integer userId) {
        Map<Avatar, Position> avatarPositionMap = new HashMap<>();
        try {
            Integer roomId = roomController.findRoomIdByUserId(userId);
            Set<Integer> allAvatarsInRoom = roomController.getAllUsersInRoom(roomId);
            for (Integer avatarId : allAvatarsInRoom) {
                Avatar avatar = avatarController.getAvatar(avatarId);
                Position position = getAvatarPosition(avatarId);
                if (avatar != null && position != null) {
                    avatarPositionMap.put(avatar, position);
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body(new PositionsResponse(avatarPositionMap, "Avatars data"));
        } catch (Exception err) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new PositionsResponse(avatarPositionMap, ServerConstants.UNEXPECTED_ERROR));
        }
    }
    public Position getAvatarPosition(Integer avatarId){
        return positions.get(avatarId);
    }
}
