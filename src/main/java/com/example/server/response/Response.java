package com.example.server.response;

import com.example.server.entities.Poster;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface Response {
    String getMessage();
//    Integer getUserId();

    static ResponseEntity<Response> badRequestResponse(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new PosterResponse(message, null, null));
    }

    static ResponseEntity<Response> serverErrorResponse(String message, Integer userId, Poster poster) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new PosterResponse(message, userId, poster));
    }
}
