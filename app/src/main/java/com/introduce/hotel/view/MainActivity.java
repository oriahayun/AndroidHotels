package com.introduce.hotel.view;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.introduce.hotel.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);



        // 첫 번째 fragment를 설정
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, new HotelListFragment())
                .commit();

        // BottomNavigationView의 아이템을 클릭할 때마다 해당하는 fragment로 교체
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.home:
                    selectedFragment = new HotelListFragment();
                    break;
                case R.id.weather:
                    selectedFragment = new WeatherFragment();
                    break;
                case R.id.add:
                    selectedFragment = new ProfileFragment();
                    break;
                case R.id.favorite:
                    selectedFragment = new ProfileFragment();
                    break;
                case R.id.profile:
                    selectedFragment = new ProfileFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, selectedFragment)
                    .commit();
            return true;
        });
    }
}