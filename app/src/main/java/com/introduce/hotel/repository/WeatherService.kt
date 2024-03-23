package com.introduce.hotel.repository

import com.introduce.hotel.model.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("forecast")
    fun getCurrentWeather(
            @Query("latitude") latitude: Double,
            @Query("longitude") longitude: Double,
            @Query("current") current: String,
            @Query("hourly") hourly: String
    ): Call<WeatherResponse>
}
