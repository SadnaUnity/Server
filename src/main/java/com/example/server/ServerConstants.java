package com.example.server;
import org.springframework.http.HttpStatus;

public class ServerConstants {
    public static final int DEFAULT_ROOM = 1;
    public static final String USER_EXISTS = "Username '%s' already exists";
    public static final String ROOMS_TABLE = "rooms";
    public static final String USERS_TABLE = "users";
    public static final String AVATAR_TABLE = "avatars";
    public static final String POSTERS_TABLE = "posters";
    public static final String ROOM_EXISTS = "Room with name: '%s' is already exist";
    public static final String POSTER_EXISTS = "Poster with name: '%s' is already exist";
    public static final String ROOM_ID_NOT_EXISTS = "ERROR! Room Id: '%s' isn't exist";
    public static final String POSTER_ID_NOT_EXISTS = "ERROR! Poster Id: '%s' isn't exist";
    public static final String USER_ID_NOT_EXISTS = "ERROR! User Id: '%s' isn't exist";
    public static final String AVATAR_NOT_EXISTS = "ERROR! Avatar for user id: '%s' isn't exist";
    public static final String ROOM_CREATED_SUCCESSFULLY = "Room: '%s' created successfully";
    public static final String USER_CHANGED_ROOM_SUCCESSFULLY = "User Id: '%s' moved into room Id: '%s' successfully";
    public static final String AVATAR_CREATED_SUCCESSFULLY = "Avatar: '%s' created successfully";
    public static final String USER_CREATED_SUCCESSFULLY = "User: '%s' created successfully";
    public static final String POSTER_CREATED_SUCCESSFULLY = "Poster: '%s' created successfully";
    public static final String POSTER_DELETED_SUCCESSFULLY = "Poster: '%s' deleted successfully";
    public static final String ROOM_DELETED_SUCCESSFULLY = "Room: '%s' deleted successfully";
    public static final String LOGIN_SUCCESSFULLY = "Login successfully";
    public static final String IMAGE_EMPTY = "Image file is empty";
    public static final String FAILED_LOAD_FILE_DATA = "Failed to load file data";
    public static final String FAILED_CREATE_AVATAR = "Failed to create avatar";
    public static final String FAILED_CREATE_POSTER = "Failed to create poster";
    public static final String INVALID_USERNAME_OR_PASSWORD = "Invalid username or password!";
    public static final String FILE_TOO_BIG = "File size exceeds the limit.";
    public static final String UNEXPECTED_ERROR = "Unexpected error has occurred";
    public static final String FILE_UPLOAD_FAILED = "Unexpected error has occurred while trying to upload the file to storage";
    public static final String UPDATE_DB_WITH_POSTER_DATA = "Unexpected error has occurred while trying to update db with new poster.";
    public static final String PROJECT_ID = "school-384409";
    public static final String CREDENTIALS_PATH = "src/main/java/com/example/server/credentialsData.json";
    static final String DATABASE_NAME = "sadna_db";
    static final String DB_USER_NAME = "root";
    static final String DB_PASSWORD = "Chxkhcmk69";
    static final String DB_HOST = "34.165.195.48";
    static final String DB_HOST_PORT = "3306";
    public static final String BUCKET_NAME = "posters-sadna";
    public static final String POS_EXCEPTED = "Pos excepted";
}