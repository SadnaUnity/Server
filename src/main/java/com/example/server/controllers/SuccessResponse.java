package com.example.server.controllers;

import com.example.server.controllers.Response;

public class SuccessResponse implements Response {
    private String message;

    public SuccessResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

