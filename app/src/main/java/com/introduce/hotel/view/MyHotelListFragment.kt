package com.introduce.hotel.view

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.introduce.hotel.R
import com.introduce.hotel.adapter.HotelAdapter
import com.introduce.hotel.database.AppDatabase
import com.introduce.hotel.viewmodel.HotelViewModel
import com.introduce.hotel.viewmodel.HotelViewModelFactory

class MyHotelListFragment : Fragment() {
    private lateinit var viewModel: HotelViewModel
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_hotel_list, container, false)
        initializeUI(rootView)
        setupViewModel()
        return rootView
    }

    private fun initializeUI(rootView: View) {
        recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView).apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 1)
            adapter = HotelAdapter(requireActivity(), mutableListOf(), true)
        }
        rootView.findViewById<Button>(R.id.addButton).setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_myHotelListFragment_to_addHotelFragment3)
        }

        rootView.findViewById<TextView>(R.id.titleTextView).text = getString(R.string.hotels)
        rootView.findViewById<TextView>(R.id.descriptionTextView).text = getString(R.string.select_a_hotel)
    }

    private fun setupViewModel() {
        val appDatabase = AppDatabase.getInstance(requireContext().applicationContext)
        val factory = HotelViewModelFactory(appDatabase.hotelDao())
        viewModel = ViewModelProvider(this, factory).get(HotelViewModel::class.java)

        viewModel.allHotels.observe(viewLifecycleOwner, Observer { hotels ->
            (recyclerView.adapter as HotelAdapter).hotels = hotels
            recyclerView.adapter?.notifyDataSetChanged()
        })

        if (isOnline()) {
            FirebaseAuth.getInstance().currentUser?.email?.let {
                viewModel.fetchMyHotelsForUserEmail(
                    it
                )
            }
        } else {
            Toast.makeText(context, "Offline: Displaying cached hotels", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isOnline(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
