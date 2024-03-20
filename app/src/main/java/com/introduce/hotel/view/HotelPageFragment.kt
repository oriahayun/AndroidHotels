package com.introduce.hotel.view

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.introduce.hotel.R
import com.introduce.hotel.database.AppDatabase
import com.introduce.hotel.database.HotelDao
import com.introduce.hotel.model.HotelEntity
import com.squareup.picasso.Picasso
import java.util.*
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class HotelPageFragment : Fragment() {
    private lateinit var map: GoogleMap
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var appDatabase: AppDatabase
    private lateinit var hotelDao: HotelDao
    private lateinit var currentHotel: HotelEntity
    private lateinit var imageView: ImageView
    private lateinit var editTextHotelName: EditText
    private lateinit var editTextHotelDescription: EditText
    private lateinit var buttonSaveEdits: Button
    private lateinit var deleteButton: Button
    private var selectedImageUri: Uri? = null
    private var selectedLocation: LatLng? = null
    companion object {
        private const val REQUEST_CODE_GALLERY = 1
    }
    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap

        val bundle = arguments
        bundle?.let {
            setCurrentHotel(bundle)
            editTextHotelName.setText(currentHotel.hotelName)
            editTextHotelDescription.setText(currentHotel.description)
            Picasso.get().load(currentHotel.imageUrl).into(imageView)
        }
    }
 
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseFirestore = FirebaseFirestore.getInstance()
        appDatabase = Room.databaseBuilder(requireContext().applicationContext,
                AppDatabase::class.java, "hotel_database").allowMainThreadQueries().build()
        hotelDao = appDatabase.hotelDao()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val callback1 = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapsFragment) as? SupportMapFragment
        mapFragment?.getMapAsync(callback)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback1)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_hotel_page, container, false)

        editTextHotelName = rootView.findViewById(R.id.editTextHotelName)
        editTextHotelDescription = rootView.findViewById(R.id.editTextHotelDescription)
        buttonSaveEdits = rootView.findViewById(R.id.buttonSaveEdits)
        deleteButton = rootView.findViewById(R.id.deleteButton)
        imageView = rootView.findViewById(R.id.hotelImage)

        return rootView
    }


    private fun setCurrentHotel(bundle: Bundle) {
        val hotelId = bundle.getString("hotelId")
        val userEmail = bundle.getString("userEmail")
        val hotelName = bundle.getString("hotelName")
        val imageUrl = bundle.getString("imageUrl")
        selectedImageUri  =  Uri.parse(imageUrl)
        val description = bundle.getString("description")
        val latitude = bundle.getDouble("latitude")
        val longitude = bundle.getDouble("longitude")
        currentHotel = HotelEntity(hotelId!!, userEmail!!,  hotelName!!, imageUrl!!, description!!, latitude, longitude)
        if (latitude != 0.0 && longitude != 0.0) {
            val hotelPosition = LatLng(latitude, longitude)
            val marker = map.addMarker(MarkerOptions().position(hotelPosition).title(hotelName).snippet(hotelId))

        }

    }
}
