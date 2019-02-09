package com.handaomo.smartsudoku.api_services;

import com.handaomo.smartsudoku.dtos.UserDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {
    @POST("login")
    Call<UserDto> login(@Body UserDto user);
}