package com.introduce.hotel.model;
import com.google.gson.annotations.SerializedName;

public class WeatherResponse {
    @SerializedName("temperature_2m")
    private float temperature;
    @SerializedName("relative_humidity_2m")
    private float humidity;
    @SerializedName("wind_speed_10m")
    private float windSpeed;

    public float getTemperature() {
        return temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getWindSpeed() {
        return windSpeed;
    }
}
