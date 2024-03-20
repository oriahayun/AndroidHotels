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

        editTextHotelName = rootView.findViewById(R.id.editTextHotelName)
        editTextHotelDescription = rootView.findViewById(R.id.editTextHotelDescription)
        buttonSaveEdits = rootView.findViewById(R.id.buttonSaveEdits)
        deleteButton = rootView.findViewById(R.id.deleteButton)
        imageView = rootView.findViewById(R.id.hotelImage)



        imageView.setOnClickListener { openGallery() }

        buttonSaveEdits.setOnClickListener {
            val newHotelName = editTextHotelName.text.toString()
            val newDescription = editTextHotelDescription.text.toString()

            selectedImageUri?.let { uri ->
                    val storageRef = FirebaseStorage.getInstance().getReference().child("hotel_images/${newHotelName}")
                    storageRef.putFile(uri)
                        .addOnSuccessListener { taskSnapshot ->
                            storageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                                currentHotel.imageUrl = imageUrl.toString()
                                firebaseFirestore.collection("Hotels").document(currentHotel.key.toString())
                                    .update("name", newHotelName, "description", newDescription,"imageUrl", imageUrl.toString(),"latitude", selectedLocation!!.latitude,"longitude", selectedLocation!!.longitude)

                                    .addOnSuccessListener {
                                        currentHotel.apply {
                                            hotelName = newHotelName
                                            description = newDescription
                                        }
                                        hotelDao.update(currentHotel)
                                        Toast.makeText(context, "Hotel updated successfully", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { Toast.makeText(context, "Error updating hotel", Toast.LENGTH_SHORT).show() }

                                Toast.makeText(context, "Image updated successfully", Toast.LENGTH_SHORT).show()

                            }
                        }
                        .addOnFailureListener { Toast.makeText(context, "Error uploading image", Toast.LENGTH_SHORT).show() }

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

    private fun openGallery() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireContext() as Activity, arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_GALLERY
            )
        } else {
            imagePickerLauncher.launch("image/*")
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        //here i am checking if the phone has granted permission to the app to access gallery
        if (requestCode == REQUEST_CODE_GALLERY) {
            imagePickerLauncher.launch("image/*")
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
        val isEdit = bundle.getBoolean("isEdit")
        if (!isEdit) {
            editTextHotelName.isEnabled = false
            editTextHotelDescription.isEnabled = false
            buttonSaveEdits.visibility = View.GONE
            deleteButton.visibility = View.GONE
            imageView.isEnabled = false
        }else{
            map.setOnMapClickListener { latLng ->
                selectedLocation = latLng
                map.clear()
                map.addMarker(MarkerOptions().position(latLng))
            }
        }
    }
}
