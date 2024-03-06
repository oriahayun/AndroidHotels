package com.introduce.hotel;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
public class HotelListActivity extends AppCompatActivity {
    String TAG = "firebase_test";
    HotelAdapter adapter;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity2_city_list);
        getSupportActionBar().hide();
        List<Hotel> cities = new ArrayList<>();
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
                                        StorageReference imageRef = storageRef.child("travel/" + imageName );
                                        try {
                                            byte[] bytes = Tasks.await ( imageRef.getBytes(Long.MAX_VALUE) );
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            List<String> galleryImagesNames = (ArrayList) document.getData().get("galleryImages");
                                            List<Bitmap> galleryImages = new ArrayList<>();
                                            for (String name: galleryImagesNames) {
                                                imageRef = storageRef.child("travel/" + name );
                                                bytes = Tasks.await(imageRef.getBytes(Long.MAX_VALUE));
                                                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                galleryImages.add(bitmap);
                                            }
                                            Hotel hotel = new Hotel(
                                                    document.getId(),
                                                    bitmap,
                                                    galleryImages,
                                                    document.getData().get("cityName").toString(),
                                                    document.getData().get("aboutDescription").toString(),
                                                    document.getData().get("top5").toString()
                                            );
                                            cities.add(hotel);
                                        } catch (ExecutionException ex) {
                                            Log.e(TAG, "Error downloading image.", ex.getCause());
                                        } catch (InterruptedException ex) {
                                            Log.e(TAG, "Task interrupted.", ex);
                                        }
                                    }
                                    runOnUiThread(()->{
                                        progressDialog.dismiss();
                                        adapter.notifyDataSetChanged();
                                    });
                                }).start();
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });

        TextView title = findViewById(R.id.titleTextView);
        title.setText("Destinations");
        TextView description = findViewById(R.id.descriptionTextView);
        description.setText("Select a destination below to see more details on each city and our top exploring recommendations");
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 1));

        adapter = new HotelAdapter(cities);
        recyclerView.setAdapter(adapter);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(
                view -> {
                    Intent intent = new Intent(HotelListActivity.this, AddHotelActivity.class);
                    startActivity(intent);
                    finish();
                }
        );
    }
}