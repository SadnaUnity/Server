package com.example.server.chat;

import com.example.server.controllers.RoomController;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ChatWebSocketHandler implements WebSocketHandler {
    private final RoomController roomController;
    private Map<WebSocketSession,Integer> usersSessions = new HashMap<>();
    private Map<Integer,List<WebSocketSession> > roomSockets = new HashMap<>();

    //TODO get room (incluse position)
    public ChatWebSocketHandler(RoomController roomController) {
        this.roomController = roomController;
    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Integer userId = Integer.valueOf(session.getHandshakeHeaders().get("userID").get(0));
        Integer roomId = roomController.getRoomId(userId);

        // Add the session to the corresponding room sockets list
        List<WebSocketSession> roomSocketsList = roomSockets.getOrDefault(roomId, new ArrayList<>());
        roomSocketsList.add(session);
        roomSockets.put(roomId, roomSocketsList);

        // Map the session to the user ID
        usersSessions.put(session, userId);
    }


    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String payload = (String) message.getPayload();
        broadcastMessage(session,payload);
    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        // Handle transport error
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        // Remove the session from usersSessions map
        Integer userID = usersSessions.remove(session);

        // Remove the session from roomSockets map
        for (Map.Entry<Integer, List<WebSocketSession>> entry : roomSockets.entrySet()) {
            List<WebSocketSession> roomSocketsList = entry.getValue();
            roomSocketsList.remove(session);
        }
    }


    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private void broadcastMessage(WebSocketSession session, String messageContent) throws IOException {
        List<WebSocketSession> sessionList = getSessionList(session);
        for (WebSocketSession webSocketSession : sessionList) {
            webSocketSession.sendMessage(new TextMessage(messageContent));
        }
    }
    private List<WebSocketSession> getSessionList(WebSocketSession session) {
        for (Map.Entry<Integer, List<WebSocketSession>> entry : roomSockets.entrySet()) {
            List<WebSocketSession> roomSocketsList = entry.getValue();
            if(roomSocketsList.contains(session)){
                return roomSocketsList;
            }
        }
        return null;
    }

    private void changeUserRoom(int userId, int newRoomId) {
        // Retrieve the list of WebSocketSession objects associated with the userId
        WebSocketSession userSession = null;
        for (Map.Entry<WebSocketSession, Integer> entry : usersSessions.entrySet()) {
            if (entry.getValue() == userId) {
                userSession = entry.getKey();
                break;
            }
        }

        //remove user session from old room
        for (Map.Entry<Integer, List<WebSocketSession>> entry : roomSockets.entrySet()) {
            List<WebSocketSession> roomSocketsList = entry.getValue();
            if(roomSocketsList.contains(userSession)){
                roomSocketsList.remove(userSession);
                break;
            }
        }

        // Update the roomSockets map with the new userId
        List<WebSocketSession> roomSocketsList = roomSockets.get(newRoomId);
        if (roomSocketsList != null) {
            roomSocketsList.add(userSession);
        } else {
            roomSocketsList = new ArrayList<>();
            roomSocketsList.add(userSession);
            roomSockets.put(newRoomId, roomSocketsList);
        }
    }

}