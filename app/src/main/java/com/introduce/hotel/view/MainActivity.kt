package com.introduce.hotel.view

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.os.Bundle
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.introduce.hotel.R

class MainActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_main)

                val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
                val navController = findNavController(this, R.id.nav_host_fragment)
                bottomNavigationView.setupWithNavController( navController)

        }

}
