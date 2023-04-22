package com.example.server;
import org.springframework.http.HttpStatus;

public class ServerConstants {
    public static final int BAD_REQUEST_RESPONSE_CODE = HttpStatus.BAD_REQUEST.value();
    public static final String USER_EXISTS = "Username '%s' already exists";
    public static final String ROOM_EXISTS = "Room with name: '%s' is already exist";
    public static final String POSTER_EXISTS = "Poster with name: '%s' is already exist";
    public static final String ROOM_ID_NOT_EXISTS = "ERROR! Room Id: '%s' isn't exist";
    public static final String POSTER_ID_NOT_EXISTS = "ERROR! Poster Id: '%s' isn't exist";
    public static final String USER_ID_NOT_EXISTS = "ERROR! User Id: '%s' isn't exist";
    public static final String AVATAR_NOT_EXISTS = "ERROR! Avatar for user id: '%s' isn't exist";
    public static final String ROOM_CREATED_SUCCESSFULLY = "Room: '%s' created successfully";
    public static final String AVATAR_CREATED_SUCCESSFULLY = "Avatar: '%s' created successfully";
    public static final String USER_CREATED_SUCCESSFULLY = "User: '%s' created successfully";
    public static final String POSTER_CREATED_SUCCESSFULLY = "Poster: '%s' created successfully";
    public static final String LOGIN_SUCCESSFULLY = "Login successfully";
    public static final String IMAGE_EMPTY = "Image file is empty";
    public static final String FAILED_LOAD_FILE_DATA = "Failed to load file data";
    public static final String FAILED_CREATE_AVATAR = "Failed to create avatar";
    public static final String INVALID_USERNAME_OR_PASSWORD = "Invalid username or password!";
    public static final String UNEXPECTED_ERROR = "Unexpected error has occurred";
}