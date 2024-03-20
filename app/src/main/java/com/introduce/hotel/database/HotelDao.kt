package com.introduce.hotel.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.introduce.hotel.model.HotelEntity

@Dao
interface HotelDao {
    @Query("SELECT * FROM HotelEntity")
    fun getAll(): LiveData<List<HotelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(hotels: List<HotelEntity>)



    @Query("DELETE FROM HotelEntity")
    fun deleteAll()

    @Delete
    fun delete(hotel: HotelEntity)

    @Update
    fun update(hotel: HotelEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(hotel: HotelEntity)
}
