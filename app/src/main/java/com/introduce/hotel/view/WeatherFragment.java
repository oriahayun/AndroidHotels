package com.introduce.hotel.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.introduce.hotel.R;
import com.introduce.hotel.model.WeatherResponse;
import com.introduce.hotel.repository.WeatherApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherFragment extends Fragment {

    private TextView temperatureTextView;
    private TextView humidityTextView;
    private TextView windSpeedTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);

        temperatureTextView = rootView.findViewById(R.id.temperatureTextView);
        humidityTextView = rootView.findViewById(R.id.humidityTextView);
        windSpeedTextView = rootView.findViewById(R.id.windSpeedTextView);

        WeatherApiClient weatherApiClient = new WeatherApiClient();
        weatherApiClient.getWeatherForecast(52.52, 13.41, "temperature_2m,wind_speed_10m", "temperature_2m,relative_humidity_2m,wind_speed_10m", new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();
                    if (weatherResponse != null) {
                        float temperature = weatherResponse.getTemperature();
                        float humidity = weatherResponse.getHumidity();
                        float windSpeed = weatherResponse.getWindSpeed();

                        temperatureTextView.setText("Temperature: " + temperature + "°C");
                        humidityTextView.setText("Humidity: " + humidity + "%");
                        windSpeedTextView.setText("Wind Speed: " + windSpeed + "m/s");
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("WeatherFragment", "Failed to fetch weather data", t);
            }
        });

        return rootView;
    }
}

