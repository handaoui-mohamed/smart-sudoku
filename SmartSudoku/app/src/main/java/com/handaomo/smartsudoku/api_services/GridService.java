package com.handaomo.smartsudoku.api_services;

import com.handaomo.smartsudoku.dtos.GridDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface GridService {
    @GET("grids/today")
    Call<GridDto> getTodayGrid();

    @GET("grids/random")
    Call<GridDto> getRandomGrid();

    @GET("grids")
    Call<List<GridDto>> getAllGrids();
}