package com.introduce.hotel.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "HotelEntity")
data class HotelEntity(
        @PrimaryKey(autoGenerate = true) var id: Int = 0,
        var key: String? = null,
        var userEmail: String,
        var hotelName: String,
        var imageUrl: String,
        var description: String,
        var latitude: Double,
        var longitude: Double
) {

    constructor(
            key: String,
            userEmail: String,
            hotelName: String,
            imageUrl: String,
            description: String,
            latitude: Double,
            longitude: Double
    ) : this(0, key,userEmail, hotelName,  imageUrl, description, latitude, longitude)


}
