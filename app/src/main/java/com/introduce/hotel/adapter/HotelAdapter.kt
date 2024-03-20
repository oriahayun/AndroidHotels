package com.introduce.hotel.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.introduce.hotel.R
import com.introduce.hotel.model.HotelEntity
import com.squareup.picasso.Picasso

class HotelAdapter(
    private val fragmentActivity: FragmentActivity,
    var hotels: List<HotelEntity>,
    private val isEdit: Boolean
) : RecyclerView.Adapter<HotelViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hotel, parent, false)
        return HotelViewHolder(view)
    }
    fun updateHotels(hotels: List<HotelEntity>) {
        this.hotels = hotels
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: HotelViewHolder, position: Int) {
        val hotel = hotels[position]

        // Load image using Picasso library
        Picasso.get().load(hotel.imageUrl).into(holder.hotelImage)

        // Set hotel name
        holder.hotelName.text = hotel.hotelName

        // Set click listener to navigate to hotel detail fragment
        holder.itemView.setOnClickListener {
            // Pass hotel data to HotelPageFragment using bundle
            val bundle = Bundle().apply {
                putString("hotelId", hotel.key)
                putString("userEmail", hotel.userEmail)
                putString("hotelName", hotel.hotelName)
                putString("imageUrl", hotel.imageUrl)
                putString("description", hotel.description)
                putDouble("latitude", hotel.latitude ?: 0.0)
                putDouble("longitude", hotel.longitude ?: 0.0)
                putBoolean("isEdit", isEdit)
            }

            // Navigate to HotelPageFragment
            val navController = fragmentActivity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            if (isEdit)
                navController.navController.navigate(R.id.action_myHotelListFragment_to_hotelPageFragment, bundle)
            else navController.navController.navigate(R.id.action_hotelListFragment_to_hotelPageFragment, bundle)
        }
    }

    override fun getItemCount(): Int {
        return hotels.size
    }
}
