package com.introduce.hotel.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

public class Hotel implements Serializable {
    private String key;
    private String imageUrl;
    private String cityName;
    private String aboutDescription;

    public Hotel(String key, String imageUrl,String cityName, String aboutDescription) {
        this.key = key;
        this.imageUrl = imageUrl;
        this.cityName = cityName;
        this.aboutDescription = aboutDescription;
    }
    public String getKey() {
        return key;
    }


    public String getAboutDescription() {
        return aboutDescription;
    }

    public String getImage() {
        return imageUrl;
    }

    public String getCityName() {
        return cityName;
    }
}
