package com.introduce.hotel.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth

class HotelPageFragment : Fragment() {
    private lateinit var map: GoogleMap
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var appDatabase: AppDatabase
    private lateinit var hotelDao: HotelDao
    private lateinit var currentHotel: HotelEntity
    private lateinit var imageView: ImageView
    private lateinit var editTextHotelName: EditText
    private lateinit var map_edittext: EditText
    private lateinit var editTextHotelDescription: EditText
    private lateinit var editTextReview: EditText
    private lateinit var textViewReview: TextView
    private lateinit var addReview: TextView
    private lateinit var buttonSaveEdits: Button
    private lateinit var deleteButton: Button
    private var selectedImageUri: Uri? = null
    private var selectedLocation: LatLng? = null
    private lateinit var mAuth: FirebaseAuth
    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap

        val bundle = arguments
        bundle?.let {
            setCurrentHotel(bundle)
            editTextHotelName.setText(currentHotel.hotelName)
            map_edittext.setText(currentHotel.address)
            textViewReview.setText(currentHotel.review)
            var userName = "";
            val user = mAuth.currentUser
            user?.let {
                userName = user.displayName.toString()
            }
            val reviews = currentHotel.review.split("\n")
            for (review in reviews) {
                if (review.startsWith("$userName:")) {
                    editTextReview.setText(review.split(':')[1])
                }
            }


            editTextHotelDescription.setText(currentHotel.description)
            Picasso.get().load(currentHotel.imageUrl).into(imageView)
            val hotelPosition = LatLng(currentHotel.latitude, currentHotel.longitude)
            val marker = map.addMarker(MarkerOptions().position(hotelPosition).title(currentHotel.hotelName).snippet(currentHotel.key))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(hotelPosition, 15f))
        }
    }
    private val imagePickerLauncher = registerForActivityResult<String, Uri>(
        ActivityResultContracts.GetContent(),
        object : ActivityResultCallback<Uri?> {
            override fun onActivityResult(result: Uri?) {
                if (result != null) {
                    try {
                        result?.let {
                            Picasso.get().load(it).into(imageView)
                            selectedImageUri = it
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    )
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
        mAuth = FirebaseAuth.getInstance()
        editTextHotelName = rootView.findViewById(R.id.editTextHotelName)
        editTextHotelDescription = rootView.findViewById(R.id.editTextHotelDescription)
        buttonSaveEdits = rootView.findViewById(R.id.buttonSaveEdits)
        deleteButton = rootView.findViewById(R.id.deleteButton)
        imageView = rootView.findViewById(R.id.hotelImage)
        editTextReview = rootView.findViewById(R.id.editTextReview)
        textViewReview = rootView.findViewById(R.id.textViewReview)
        addReview = rootView.findViewById(R.id.addReview)
        imageView.setOnClickListener { openGallery() }
        map_edittext = rootView.findViewById(R.id.map_edittext)
        buttonSaveEdits.setOnClickListener {
            val newHotelName = editTextHotelName.text.toString()
            val newDescription = editTextHotelDescription.text.toString()
            val user = mAuth.currentUser
            var userName = "";
            user?.let {
                userName = user.displayName.toString()
            }
            val newAddress = map_edittext.text.toString()
            val regex = Regex("$userName:.*")
            val existingText = textViewReview.text.toString()
            var newReview  = editTextReview.text.toString()
            if (newReview.isEmpty()){
                newReview = existingText
            }else{
                newReview = if (existingText.contains(regex)) {
                    existingText.replace(regex, "$userName: $newReview")
                } else {
                    "$existingText\n$userName: $newReview"
                }
            }

            if(selectedImageUri == null){
                val latitude1 = if (selectedLocation != null) selectedLocation!!.latitude else currentHotel.latitude
                val longitude1 = if (selectedLocation != null) selectedLocation!!.longitude else currentHotel.longitude
                firebaseFirestore.collection("Hotels").document(currentHotel.key.toString())
                    .update("name", newHotelName,
                        "description", newDescription,
                        "review",newReview,
                        "imageUrl",    currentHotel.imageUrl.toString(),
                        "address",newAddress,
                        "latitude",latitude1 , "longitude",longitude1

                    )
                    .addOnSuccessListener {
                        currentHotel.apply {
                            hotelName = newHotelName
                            description = newDescription
                            review = newReview
                            address = newAddress
                            latitude = latitude1
                            longitude = longitude1
                        }
                        hotelDao.update(currentHotel)
                        textViewReview.setText(currentHotel.review)
                        Toast.makeText(context, "Hotel updated successfully", Toast.LENGTH_SHORT).show()
                        NavHostFragment.findNavController(this).popBackStack()
                    }
                    .addOnFailureListener { Toast.makeText(context, "Error updating hotel", Toast.LENGTH_SHORT).show() }

            }else{
                selectedImageUri?.let { uri ->
                    val storageRef = FirebaseStorage.getInstance().getReference().child("hotel_images/${newHotelName}")
                    storageRef.putFile(uri)
                        .addOnSuccessListener { taskSnapshot ->
                            storageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                                currentHotel.imageUrl = imageUrl.toString()
                                val latitude1 = if (selectedLocation != null) selectedLocation!!.latitude else currentHotel.latitude
                                val longitude1 = if (selectedLocation != null) selectedLocation!!.longitude else currentHotel.longitude
                                firebaseFirestore.collection("Hotels").document(currentHotel.key.toString())
                                    .update("name", newHotelName,
                                        "description",
                                        newDescription,
                                        "review",newReview,
                                        "imageUrl", imageUrl.toString(),
                                        "address", newAddress,
                                        "latitude",latitude1 , "longitude",longitude1)

                                    .addOnSuccessListener {
                                        currentHotel.apply {
                                            hotelName = newHotelName
                                            description = newDescription
                                            address = newAddress
                                            review = newReview
                                            latitude = latitude1
                                            longitude = longitude1
                                        }
                                        hotelDao.update(currentHotel)
                                        textViewReview.setText(currentHotel.review)
                                        Toast.makeText(context, "Hotel updated successfully", Toast.LENGTH_SHORT).show()
                                        NavHostFragment.findNavController(this).popBackStack()
                                    }
                                    .addOnFailureListener { Toast.makeText(context, "Error updating hotel", Toast.LENGTH_SHORT).show() }

                                Toast.makeText(context, "Image updated successfully", Toast.LENGTH_SHORT).show()

                            }
                        }
                        .addOnFailureListener { Toast.makeText(context, "Error uploading image", Toast.LENGTH_SHORT).show() }

                }
            }


        }

        deleteButton.setOnClickListener {
            currentHotel.key?.let { hotelKey ->
                firebaseFirestore.collection("Hotels").document(hotelKey)
                        .delete()
                        .addOnSuccessListener {
                            hotelDao.delete(currentHotel)
                            Toast.makeText(context, "Hotel deleted successfully", Toast.LENGTH_SHORT).show()
                            activity?.onBackPressed()
                        }
                        .addOnFailureListener { Toast.makeText(context, "Error deleting hotel", Toast.LENGTH_SHORT).show() }
            }
        }

        return rootView
    }
    private fun selectAddress() {
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(requireContext())
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                val address = place.address
                map_edittext.setText(address)
                val latitude = place.latLng?.latitude
                val longitude = place.latLng?.longitude
                if (latitude != null && longitude != null) {
                    selectedLocation = LatLng(latitude, longitude)
                    map.clear()
                    map.addMarker(MarkerOptions().position(selectedLocation!!).title(address))
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation!!, 15f))
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
            }
        }
    }
    private fun openGallery() {
        imagePickerLauncher.launch("image/*")
    }

    private fun setCurrentHotel(bundle: Bundle) {
        val hotelId = bundle.getString("hotelId")
        val userEmail = bundle.getString("userEmail")
        val hotelName = bundle.getString("hotelName")
        val imageUrl = bundle.getString("imageUrl")
        val description = bundle.getString("description")
        val review = bundle.getString("review") ?: ""
        val latitude = bundle.getDouble("latitude")
        val longitude = bundle.getDouble("longitude")
        val address = bundle.getString("address") ?:""
        currentHotel = HotelEntity(hotelId!!,
            userEmail!!,
            hotelName!!,
            imageUrl!!,
            description!!,
            review!!,
            latitude,
            longitude,
             address)

        if (latitude != 0.0 && longitude != 0.0) {
            val hotelPosition = LatLng(latitude, longitude)
            val marker = map.addMarker(MarkerOptions().position(hotelPosition).title(hotelName).snippet(hotelId))

        }
        val isEdit = bundle.getBoolean("isEdit")
        if (!isEdit) {
            editTextHotelName.isEnabled = false
            editTextHotelDescription.isEnabled = false
            deleteButton.visibility = View.GONE
            imageView.isEnabled = false
        }else{
            map_edittext.setOnClickListener { selectAddress() }
            editTextReview.visibility = View.GONE
            addReview.visibility = View.GONE

        }
    }
}
