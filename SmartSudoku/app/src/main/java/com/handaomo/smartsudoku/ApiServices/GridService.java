package com.handaomo.smartsudoku.ApiServices;

import com.handaomo.smartsudoku.DTO.GridDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface GridService {
    @GET("grids/random")
    Call<GridDto> getRandomGrid();

    @GET("grids")
    Call<List<GridDto>> getAllGrids();
}