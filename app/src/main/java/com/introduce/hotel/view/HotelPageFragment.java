package com.introduce.hotel.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.introduce.hotel.R;
import com.introduce.hotel.model.Hotel;

public class HotelPageFragment extends Fragment {
    private Hotel currentHotel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_hotel_page, container, false);

        // Retrieve data that was sent from previous activity
        currentHotel = currentHotel;

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(currentHotel.getCityName());
            }
        }

        TextView aboutTitle = rootView.findViewById(R.id.aboutTitle);
        aboutTitle.setText("About");

        TextView aboutBody = rootView.findViewById(R.id.aboutBody);
        aboutBody.setText(currentHotel.getAboutDescription());

        return rootView;
    }
}
