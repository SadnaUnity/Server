package com.example.server.response;

public class LoginResponse implements Response {
    private String user_id;
    private String message;
    public LoginResponse(String message, String user_id) {
        this.message = message;
        this.user_id = user_id;
    }

    public String getMessage() {
        return message;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}