package com.handaomo.smartsudoku.dtos;

public class UserDto {
    public String first_name;
    public String last_name;
    public String password;
    public String username;

    public UserDto(){}

    public UserDto(String username, String password){
        this.username = username;
        this.password = password;
    }
}
