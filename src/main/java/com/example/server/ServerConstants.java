package com.example.server;
import org.springframework.http.HttpStatus;

public class ServerConstants {
    public static final int BAD_REQUEST_RESPONSE_CODE = HttpStatus.BAD_REQUEST.value();
    public static final String USER_EXISTS_MESSAGE = "Username '%s' already exists";
    public static final String INVALID_USERNAME_OR_PASSWORD_MESSAGE = "Invalid username or password!";
}