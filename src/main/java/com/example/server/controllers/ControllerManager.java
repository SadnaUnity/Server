package com.example.server.controllers;

import com.example.server.entities.Avatar;
import com.example.server.entities.Poster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class ControllerManager {
    private AvatarController avatarController;
    private LoginController loginController;
    private MovingController movingController;
    private PosterController posterController;
    private RoomController roomController;
    private ChatController chatController;

    @Autowired
    public ControllerManager(AvatarController avatarController, LoginController loginController, MovingController movingController, PosterController posterController, RoomController roomController, ChatController chatController) {
        this.avatarController = avatarController;
        this.loginController = loginController;
        this.movingController = movingController;
        this.posterController = posterController;
        this.roomController = roomController;
        this.chatController = chatController;
    }
    public Avatar addNewAvatarToSystem(Integer userId, Avatar.Color color, Avatar.Accessory accessory) {
        return avatarController.addNewAvatarToSystem(userId, color, accessory);
    }
    public void addUserToRoom(Integer userId, int defaultRoom) {
        roomController.insertUserIdIntoRoom(userId, defaultRoom);
    }
    public Avatar getAvatar(Integer userId) {
        return avatarController.getAvatar(userId);
    }
    public Integer findRoomIdByUserId(Integer userId) {
        return roomController.findRoomIdByUserId(userId);
    }
    public Set<Integer> getAllUsersInRoom(Integer roomId) {
        return roomController.getAllUsersInRoom(roomId);
    }
    public List<Poster> getAllPostersInRoom(Integer roomId) {
        return posterController.getAllPostersInRoom(roomId);
    }
//    public void removeUserFromChatRoom(Integer userId) {
//        chatController.removeUserFromChatRoom(userId);
//    }
//    public void addUserIntoChatRoom(Integer userId, Integer roomId) {
//        chatController.addUserIntoChatRoom(userId,roomId);
//    }
    public void logout(Integer userId){
        roomController.removeUserFromRoom(userId);
    }
    public boolean isUserOnline(Integer userId) {
        return roomController.isUserOnline(userId);
    }
    public Integer getRoomManager(Integer roomId) {
        return roomController.getRoomManager(roomId);
    }
    public String getUserName(Integer userId){
        return loginController.getUserName(userId);
    }
    public Boolean isUserInRoom(Integer userId, Integer roomId){
        Boolean res = roomController.getAllUsersInRoom(roomId).contains(userId);
        return res;
    }

}
