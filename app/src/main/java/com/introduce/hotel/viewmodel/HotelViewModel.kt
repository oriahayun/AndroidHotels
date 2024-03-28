package com.introduce.hotel.viewmodel;

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.introduce.hotel.model.HotelEntity
import com.introduce.hotel.database.HotelDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HotelViewModel(private val hotelDao: HotelDao) : ViewModel() {
    val allHotels: LiveData<List<HotelEntity>> = hotelDao.getAll()
    fun fetchHotelsFromFirestore() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = FirebaseFirestore.getInstance()
            db.collection("Hotels").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val hotels = mutableListOf<HotelEntity>() // List of hotels to be fetched

                    for (document in task.result!!) {
                        val imageName = document.getString("imageUrl")

                        val hotel = HotelEntity(
                            key = document.id,
                            userEmail = document.getString("userEmail") ?: "",
                            hotelName = document.getString("name") ?: "",
                            imageUrl = imageName ?: "",
                            description = document.getString("description") ?: "",
                            review = document.getString("review") ?: "",
                            latitude = document.getDouble("latitude") ?: 0.0,
                            longitude = document.getDouble("longitude") ?: 0.0,
                            address =  document.getString("address") ?: ""
                        )
                        hotels.add(hotel)
                    }

                    // Insert hotels into database within IO scope
                    CoroutineScope(Dispatchers.IO).launch {
                        hotelDao.deleteAll()
                        hotelDao.insertAll(hotels)
                    }.invokeOnCompletion {
                        // Do something after insertion completes if needed
                    }
                } else {
                }
            }
        }
    }

    fun fetchMyHotelsForUserEmail(userEmail: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = FirebaseFirestore.getInstance()
            db.collection("Hotels").whereEqualTo("userEmail", userEmail).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val hotels = mutableListOf<HotelEntity>() // List of hotels to be fetched

                    for (document in task.result!!) {
                        val imageName = document.getString("imageUrl")

                        val hotel = HotelEntity(
                            key = document.id,
                            userEmail = document.getString("userEmail") ?: "",
                            hotelName = document.getString("name") ?: "",
                            imageUrl = imageName ?: "",
                            description = document.getString("description") ?: "",
                            review = document.getString("review") ?: "",
                            latitude = document.getDouble("latitude") ?: 0.0,
                            longitude = document.getDouble("longitude") ?: 0.0,
                            address =  document.getString("address") ?: ""
                        )
                        hotels.add(hotel)
                    }

                    // Insert hotels into database within IO scope
                    CoroutineScope(Dispatchers.IO).launch {
                        hotelDao.deleteAll()
                        hotelDao.insertAll(hotels)
                    }.invokeOnCompletion {
                        // Do something after insertion completes if needed
                    }
                } else {
                    Log.e("HotelRepository", "Error fetching hotels for user email: $userEmail")
                }
            }
        }

    }
}