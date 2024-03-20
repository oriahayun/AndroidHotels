package com.introduce.hotel.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.introduce.hotel.database.HotelDao

class HotelViewModelFactory(private val hotelDao: HotelDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HotelViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HotelViewModel(hotelDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
