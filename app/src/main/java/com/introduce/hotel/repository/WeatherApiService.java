package com.introduce.hotel.repository;
import com.introduce.hotel.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {
    @GET("v1/forecast")
    Call<WeatherResponse> getWeatherForecast(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("current") String currentParams,
            @Query("hourly") String hourlyParams
    );
}
