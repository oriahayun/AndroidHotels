package com.introduce.hotel.view

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.introduce.hotel.R
import java.io.IOException

class MapsFragment : Fragment() {

    private lateinit var map: GoogleMap
    private lateinit var searchField: EditText
    private lateinit var searchButton: Button

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        setupMapListeners()
        loadHotels()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)
        initializeViews(view)
        setupButtonListeners()
        return view
    }

    private fun initializeViews(view: View) {
        searchField = view.findViewById(R.id.searchHotelName)
        searchButton = view.findViewById(R.id.searchButton)
    }

    private fun setupButtonListeners() {
        searchButton.setOnClickListener { searchLocation() }
    }

    private fun searchLocation() {
        val location = searchField.text.toString()
        if (location.isNotEmpty()) {
            val geocoder = context?.let { Geocoder(it) }
            try {
                val addresses = geocoder?.getFromLocationName(location, 1)
                if (addresses?.isNotEmpty() == true) {
                    val address = addresses?.get(0)
                    val latLng = address?.latitude?.let { LatLng(it, address.longitude) }
                    latLng?.let { CameraUpdateFactory.newLatLngZoom(it, 12f) }?.let { map.moveCamera(it) }
                } else {
                    Toast.makeText(context, "Not a valid address", Toast.LENGTH_LONG).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun setupMapListeners() {
        map.setOnMarkerClickListener { marker ->
            val bundle = Bundle()
            val document = marker.tag as? QueryDocumentSnapshot
            document?.let {
                val hotelId = document.id
                val hotelName = document.getString("hotelName") ?: ""
                val userEmail = document.getString("userEmail") ?: ""
                val imageUrl = document.getString("imageUrl") ?: ""
                val description = document.getString("description") ?: ""
                val latitude = document.getDouble("latitude") ?: 0.0
                val longitude = document.getDouble("longitude") ?: 0.0
                bundle.putString("hotelId", hotelId)
                bundle.putString("hotelName", hotelName)
                bundle.putString("userEmail", userEmail)
                bundle.putString("imageUrl", imageUrl)
                bundle.putString("description", description)
                bundle.putDouble("latitude", latitude)
                bundle.putDouble("longitude", longitude)
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_mapsFragment_to_hotelPageFragment, bundle)
            }
            false
        }
    }

    private fun loadHotels() {
        val db = FirebaseFirestore.getInstance()

        db.collection("Hotels").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                try {
                    for (document in task.result!!) {
                        val hotelId = document.id
                        val hotelName = document.getString("hotelName") ?: ""
                        val latitude = document.getDouble("latitude") ?: 0.0
                        val longitude = document.getDouble("longitude") ?: 0.0

                        if (latitude != 0.0 && longitude != 0.0) {
                            val hotelPosition = LatLng(latitude, longitude)
                            val marker = map.addMarker(MarkerOptions().position(hotelPosition).title(hotelName).snippet(hotelId))
                            marker?.tag = document // 마커에 Firestore 문서 추가
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error loading hotels", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Failed to fetch hotels", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapsFragment) as? SupportMapFragment
        mapFragment?.getMapAsync(callback)
    }
}
