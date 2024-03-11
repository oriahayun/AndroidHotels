package com.introduce.hotel.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

public class Hotel implements Serializable {
    private String key;
    private Bitmap mainImage;
    private String cityName;
    private String aboutDescription;

    public Hotel(String key, Bitmap mainImage,String cityName, String aboutDescription) {
        this.key = key;
        this.mainImage = mainImage;
        this.cityName = cityName;
        this.aboutDescription = aboutDescription;
    }
    public String getKey() {
        return key;
    }


    public String getAboutDescription() {
        return aboutDescription;
    }

    public Bitmap getImage() {
        return mainImage;
    }

    public String getCityName() {
        return cityName;
    }
}
