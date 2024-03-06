package com.introduce.hotel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

public class HotelPageActivity extends AppCompatActivity {
    public static Hotel currentHotel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity3_city_page);



        // Retrieve data that was sent from previous activity
//        Bundle bundle = getIntent().getExtras();
        Hotel hotel = HotelPageActivity.currentHotel; //(City) bundle.getSerializable("City");
        getSupportActionBar().setTitle(hotel.getCityName());

        RecyclerView recyclerView = findViewById(R.id.galleryRecyclerView);
        recyclerView.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new ImageGalleryAdapter(hotel.getGalleryImages()));

        TextView aboutTitle = findViewById(R.id.aboutTitle);
        aboutTitle.setText("About");
        TextView aboutBody = findViewById(R.id.aboutBody);
        aboutBody.setText(hotel.getAboutDescription());

        TextView exploreTitle = findViewById(R.id.exploreTitle);
        exploreTitle.setText("Top 5 Things to Explore");
        TextView exploreBody = findViewById(R.id.exploreBody);
        exploreBody.setText(hotel.getTop5());




    }
}