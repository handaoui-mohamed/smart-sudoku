package com.handaomo.smartsudoku.services;

import com.handaomo.smartsudoku.dtos.GridDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface GridService {
    @GET("grids/random")
    Call<GridDto> getRandomGrid();

    @GET("grids")
    Call<List<GridDto>> getAllGrids();
}