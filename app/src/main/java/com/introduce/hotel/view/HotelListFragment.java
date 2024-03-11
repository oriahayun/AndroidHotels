package com.introduce.hotel.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.introduce.hotel.R;
import com.introduce.hotel.model.Hotel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HotelListFragment extends Fragment {
    String TAG = "firebase_test";
    HotelAdapter adapter;
    ProgressDialog progressDialog;
    List<Hotel> hotels = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_hotel_list, container, false);

        TextView title = rootView.findViewById(R.id.titleTextView);
        title.setText("Hotels");

        TextView description = rootView.findViewById(R.id.descriptionTextView);
        description.setText("Select a hotel below to see more details on each city and our top exploring recommendations");

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));

        adapter = new HotelAdapter(hotels);
        recyclerView.setAdapter(adapter);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Button addButton = rootView.findViewById(R.id.addButton);
        addButton.setOnClickListener(
                view -> {
                    NavHostFragment.findNavController(HotelListFragment.this)
                            .navigate(R.id.action_hotelListFragment_to_addHotelFragment3);

                }
        );

        fetchData();

        return rootView;
    }

    private void fetchData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        progressDialog.show();

        db.collection("cities").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String imageName = document.getString("mainImage");
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference().child("travel/" + imageName);

                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Hotel hotel = new Hotel(
                                document.getId(),
                                uri.toString(),
                                document.getString("cityName"),
                                document.getString("aboutDescription")
                        );
                        hotels.add(hotel);
                        adapter.notifyDataSetChanged();
                    }).addOnFailureListener(exception -> {
                        // Handle any errors
                    });
                }
                progressDialog.dismiss();
            } else {
                // Handle the error
                progressDialog.dismiss();
            }
        });
    }
}
