package com.introduce.hotel.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.introduce.hotel.R

class HotelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val cardView: CardView = itemView.findViewById(R.id.cardView)
    val hotelImage: ImageView = itemView.findViewById(R.id.hotelImage)
    val hotelName: TextView = itemView.findViewById(R.id.hotelName)
}
