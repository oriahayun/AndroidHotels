package com.introduce.hotel;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HotelAdapter extends RecyclerView.Adapter<HotelViewHolder> {
    List<Hotel> Cities;
    public HotelAdapter(List<Hotel> cities) {
        Cities = cities;
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // takes xml file, inflates it to the screen, creates view from the layout so we can show it to the user
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.destination,parent,false);
        // only create view holder once
        HotelViewHolder viewHolder = new HotelViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        // use this function to put data on the UI
        Hotel hotel = Cities.get(position);

        holder.cityImage.setImageBitmap(hotel.getImage());
        holder.cityName.setText(hotel.getCityName());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // intent is used to navigate to a new activity
                Intent intent = new Intent(view.getContext(), HotelPageActivity.class);

                // send data to MainActivity2:
//                intent.putExtra("City", city);
                HotelPageActivity.currentHotel = hotel;
                // start new activity in our current context:
                // view.getContext tells us which activity is running this view currently
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Cities.size();
    }
}
