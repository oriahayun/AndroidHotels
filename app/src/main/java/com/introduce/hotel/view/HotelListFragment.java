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
    List<Hotel> cities = new ArrayList<>();

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

        adapter = new HotelAdapter(cities);
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
        db.collection(("cities"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            new Thread(() -> {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    FirebaseStorage storage = FirebaseStorage.getInstance();
                                    StorageReference storageRef = storage.getReference();
                                    String imageName = document.getData().get("mainImage").toString();
                                    StorageReference imageRef = storageRef.child("travel/" + imageName);
                                    try {
                                        byte[] bytes = Tasks.await(imageRef.getBytes(Long.MAX_VALUE));
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        Hotel hotel = new Hotel(
                                                document.getId(),
                                                bitmap,
                                                document.getData().get("cityName").toString(),
                                                document.getData().get("aboutDescription").toString()
                                        );
                                        cities.add(hotel);
                                    } catch (ExecutionException ex) {
                                        Log.e(TAG, "Error downloading image.", ex.getCause());
                                    } catch (InterruptedException ex) {
                                        Log.e(TAG, "Task interrupted.", ex);
                                    }
                                }
                                getActivity().runOnUiThread(() -> {
                                    progressDialog.dismiss();
                                    adapter.notifyDataSetChanged();
                                });
                            }).start();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}
