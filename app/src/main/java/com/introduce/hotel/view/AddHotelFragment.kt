package com.introduce.hotel.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.introduce.hotel.R
import com.introduce.hotel.database.AppDatabase
import com.introduce.hotel.model.HotelEntity
import com.squareup.picasso.Picasso

class AddHotelFragment : Fragment(), OnMapReadyCallback {
    val REQUEST_CODE_GALLERY = 999
    private lateinit var hotelNameEditText: EditText
    private lateinit var hotelDescriptionEditText: EditText
    private lateinit var map_edittext: EditText
    private lateinit var hotelImageView: ImageView
    private lateinit var addHotelButton: Button
    private lateinit var cancelButton: Button
    private lateinit var map: GoogleMap
    private var selectedImageUri: Uri? = null
    private var selectedLocation: LatLng? = null

    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var localDb: AppDatabase
    private val imagePickerLauncher = registerForActivityResult<String, Uri>(
        ActivityResultContracts.GetContent(),
        object : ActivityResultCallback<Uri?> {
            override fun onActivityResult(result: Uri?) {
                if (result != null) {
                    try {
                        result?.let {
                            Picasso.get().load(it).into(hotelImageView, object : com.squareup.picasso.Callback {
                                override fun onSuccess() {
                                    selectedImageUri = it
                                }
                                override fun onError(e: Exception?) {
                                    Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
                                    e?.printStackTrace()
                                }
                            })
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
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        localDb = AppDatabase.getInstance(requireContext())
    }
    private val AUTOCOMPLETE_REQUEST_CODE = 1

    private fun selectAddress() {
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(requireContext())
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

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
    @SuppressLint("MissingPermission")
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_add_hotel, container, false)
        initializeViews(rootView)
        setupMapFragment()
        setupListeners()
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLocation = LatLng(location.latitude, location.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
            } else {
                Toast.makeText(requireContext(), "Failed to get current location", Toast.LENGTH_SHORT).show()
            }
        }
        return rootView
    }

    private fun initializeViews(rootView: View) {
        hotelNameEditText = rootView.findViewById(R.id.hotelName)
        hotelDescriptionEditText = rootView.findViewById(R.id.description)
        hotelImageView = rootView.findViewById(R.id.hotelImage)
        addHotelButton = rootView.findViewById(R.id.addHotelButton)
        map_edittext = rootView.findViewById(R.id.map_edittext)
        cancelButton = rootView.findViewById(R.id.cancelButton)
    }

    private fun setupMapFragment() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapsFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun setupListeners() {
        hotelImageView.setOnClickListener { selectImage() }
        addHotelButton.setOnClickListener { uploadHotelData() }
        cancelButton.setOnClickListener { NavHostFragment.findNavController(this).popBackStack() }
        map_edittext.setOnClickListener { selectAddress() }
    }

    private fun selectImage() {
        imagePickerLauncher.launch("image/*")
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        // Allow user to select a location from the map
        map.setOnMapClickListener { latLng ->
            selectedLocation = latLng
            map.clear()
            map.addMarker(MarkerOptions().position(latLng))
        }
        map.getUiSettings().setZoomControlsEnabled(true);
    }

    private fun uploadHotelData() {


        val hotelName = hotelNameEditText.text.toString().trim()
        val hotelDescription = hotelDescriptionEditText.text.toString().trim()
        val hotelAddress = map_edittext.text.toString().trim()
        if (hotelName.isEmpty() || hotelDescription.isEmpty() || hotelAddress.isEmpty() || selectedImageUri == null || selectedLocation == null) {
            Toast.makeText(requireContext(), "All fields and location must be filled", Toast.LENGTH_SHORT).show()
            return
        }
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Adding hotel...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        // First, upload the image to Firebase Storage
        val imageRef: StorageReference = storage.reference.child("hotel_images/${hotelName}.jpg")
        imageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    imageRef.downloadUrl
                            .addOnSuccessListener { uri ->
                                // Then, save hotel data including the image URL to Firestore
                                val hotel = HashMap<String, Any>()
                                hotel["name"] = hotelName
                                hotel["description"] = hotelDescription
                                hotel["address"] = hotelAddress
                                hotel["review"] = ""
                                hotel["imageUrl"] = uri.toString()
                                hotel["latitude"] = selectedLocation!!.latitude
                                hotel["longitude"] = selectedLocation!!.longitude
                                hotel["userEmail"] = FirebaseAuth.getInstance().currentUser?.email!!


                                firestore.collection("Hotels")
                                        .add(hotel)
                                        .addOnSuccessListener { documentReference ->
                                            progressDialog.dismiss()
                                            Toast.makeText(requireContext(), "Hotel added successfully", Toast.LENGTH_SHORT).show()
                                            NavHostFragment.findNavController(this).popBackStack()

                                            // Optionally, you can also save this hotel data to Room database for offline access
                                            saveHotelToLocalDatabase(
                                                HotelEntity(
                                                    documentReference.id,
                                                    FirebaseAuth.getInstance().currentUser?.email!!,
                                                    hotelName,
                                                    uri.toString(),
                                                    hotelDescription,
                                                    "",
                                                    selectedLocation!!.latitude,
                                                    selectedLocation!!.longitude,
                                                    hotelAddress
                                                ))
                                        }
                                        .addOnFailureListener { e ->
                                            progressDialog.dismiss()
                                            Toast.makeText(requireContext(), "Failed to add hotel", Toast.LENGTH_SHORT).show()
                                        }
                            }
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
    private fun saveHotelToLocalDatabase(hotelEntity: HotelEntity) {
        Thread { localDb.hotelDao().insert(hotelEntity) }.start()
    }
}
