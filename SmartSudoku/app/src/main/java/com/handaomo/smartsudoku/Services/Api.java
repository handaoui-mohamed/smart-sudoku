package com.handaomo.smartsudoku.Services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {
    public static UserService userService;
    public static GridService gridService;

    private Api(){}

    static public void init(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://smart-sudoku.herokuapp.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userService = retrofit.create(UserService.class);
        gridService = retrofit.create(GridService.class);
    }

}
