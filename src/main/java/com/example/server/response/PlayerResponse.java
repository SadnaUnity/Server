package com.example.server.response;

import com.example.server.entities.Position;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class PlayerResponse {
    private LoginResponse playerDto;
    private Position positionDto;

    public PlayerResponse(){}
    public PlayerResponse(LoginResponse playerDto, Position positionDto)
    {
        this.playerDto = playerDto;
        this.positionDto = positionDto;
    }
    public void setPlayerDto(LoginResponse playerDto) {
        this.playerDto = playerDto;
    }

    public void setPositionDto(Position positionDto) {
        this.positionDto = positionDto;
    }

    public LoginResponse getPlayerDto() {
        return playerDto;
    }

    public Position getPositionDto() {
        return positionDto;
    }
    @JsonIgnore
    public Integer getId()
    {
        return playerDto.getUserId();
    }
}
