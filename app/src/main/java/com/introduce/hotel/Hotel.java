package com.introduce.hotel;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

public class Hotel implements Serializable {
    private String key;
    private List<Bitmap> galleryImages;
    private Bitmap mainImage;
    private String cityName;
    private String aboutDescription;
    private String top5;

    public Hotel(String key, Bitmap mainImage, List<Bitmap> galleryImages, String cityName, String aboutDescription,
                 String top5) {
        this.key = key;
        this.mainImage = mainImage;
        this.galleryImages = galleryImages;
        this.cityName = cityName;
        this.aboutDescription = aboutDescription;
        this.top5 = top5;
    }
    public String getKey() {
        return key;
    }
    public List<Bitmap> getGalleryImages() {
        return galleryImages;
    }

    public String getTop5() {
        return top5;
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
