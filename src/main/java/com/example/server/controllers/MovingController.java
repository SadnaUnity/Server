package com.example.server.controllers;

import com.example.server.ServerConstants;
import com.example.server.entities.Avatar;
import com.example.server.entities.Position;
import com.example.server.response.LoginResponse;
import com.example.server.response.PlayerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MovingController {
    Map<Integer, PlayerResponse> positions;

    public MovingController()
    {
        positions = new HashMap<Integer, PlayerResponse>();
    }
    @PostMapping("/updatePosition")
    public ResponseEntity<String> updatePosition(@RequestBody PlayerResponse posData) {
        positions.put(posData.getId(), posData);
        return ResponseEntity.status(HttpStatus.OK).body("good");

    }
    @GetMapping("/getPositions")
    public ResponseEntity<Map<Integer, PlayerResponse>> getPositions()
    {
        return ResponseEntity.status(HttpStatus.OK).body(positions);
    }
}