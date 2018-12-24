package com.handaomo.smartsudoku.Services;

import com.handaomo.smartsudoku.DTO.UserDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {
    @POST("login")
    Call<UserDto> login(@Body UserDto user);
}