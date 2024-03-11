package com.introduce.hotel.repository;
import com.introduce.hotel.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherApiClient {
    private static final String BASE_URL = "https://api.open-meteo.com/";
    private WeatherApiService apiService;

    public WeatherApiClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(WeatherApiService.class);
    }

    public void getWeatherForecast(double latitude, double longitude, String currentParams, String hourlyParams, Callback<WeatherResponse> callback) {
        Call<WeatherResponse> call = apiService.getWeatherForecast(latitude, longitude, currentParams, hourlyParams);
        call.enqueue(callback);
    }
}
