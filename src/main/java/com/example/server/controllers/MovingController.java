package com.example.server.controllers;

import com.example.server.ServerConstants;
import com.example.server.entities.Avatar;
import com.example.server.entities.AvatarPosition;
import com.example.server.entities.Position;
import com.example.server.response.PositionsResponse;
import com.example.server.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Component
public class MovingController {
    Map<Integer, Position> positions;
    private final ControllerManager controllerManager;

    @Autowired
    public MovingController(@Lazy ControllerManager controllerManager) {
        this.controllerManager = controllerManager;
        positions = new HashMap<>();
    }

    @PostMapping("/updatePosition")
    public ResponseEntity<String> updatePosition(@RequestBody Position posData, @RequestParam Integer userId) {
        positions.put(userId, posData);
        return ResponseEntity.status(HttpStatus.OK).body(ServerConstants.POSITION_UPDATED_SUCCESSFULLY);
    }
    @GetMapping("/getPositions")
    public ResponseEntity<Response> getPositions(@RequestParam Integer userId) {
        List<AvatarPosition> avatarPositionList = new ArrayList<>();
        try {
            Integer roomId = controllerManager.findRoomIdByUserId(userId);
            Set<Integer> allAvatarsInRoom = controllerManager.getAllUsersInRoom(roomId);
            for (Integer avatarId : allAvatarsInRoom) {
                Avatar avatar = controllerManager.getAvatar(avatarId);
                Position position = getAvatarPosition(avatarId);
                if (avatar != null && position != null) {
                    avatarPositionList.add(new AvatarPosition(avatar, position));
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body(new PositionsResponse(avatarPositionList, "Avatars data"));
        } catch (Exception err) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new PositionsResponse(avatarPositionList, ServerConstants.UNEXPECTED_ERROR));
        }
    }
    public Position getAvatarPosition(Integer avatarId){
        return positions.get(avatarId);
    }
}
