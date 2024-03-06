package com.introduce.hotel;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AddHotelActivity extends AppCompatActivity {
    private String selectedImagesNames ;
    private Uri[] selectedImages = new Uri[6];
    private ImageView[] imageViews = new ImageView[6];
    private void selectImages(int req_code) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), req_code);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddHotelActivity.this, HotelListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
                // Single image selected
            Uri imageUri = data.getData();
            selectedImages[requestCode] = imageUri;
            imageViews[requestCode].setImageURI(imageUri);

        }
    }
    private void uploadImagesToFirebaseStorage() {
        for (int i = 0; i < selectedImages.length; i++) {
            Uri imageUri = selectedImages[i];
            String fileName =selectedImagesNames+ ".jpg";

            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("travel/" + fileName);
            UploadTask uploadTask = storageRef.putFile(imageUri);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Image uploaded successfully
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Image upload failed
                }
            });
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hotel);
        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 11);
        } else {
        }

        imageViews[0] = findViewById(R.id.cityImage);

        imageViews[0].setOnClickListener(v->{
            selectImages(0);
        });
        imageViews[1].setOnClickListener(v->{
            selectImages(1);
        });
        imageViews[2].setOnClickListener(v->{
            selectImages(2);
        });
        imageViews[3].setOnClickListener(v->{
            selectImages(3);
        });
        imageViews[4].setOnClickListener(v->{
            selectImages(4);
        });
        imageViews[5].setOnClickListener(v->{
            selectImages(5);
        });
        Button addButton = findViewById(R.id.add_destination);
        addButton.setOnClickListener(
                view -> {
                    selectedImagesNames = ((EditText)findViewById(R.id.cityName)).getText().toString();

                    if(selectedImagesNames.isEmpty() || selectedImages == null) {
                        Toast.makeText(this, "Enter the content correctly.", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    uploadImagesToFirebaseStorage();
                    
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    // Create a new user with a first and last name
                    Map<String, Object> user = new HashMap<>();
                    user.put("mainImage", selectedImagesNames+".jpg");

                    user.put("cityName", selectedImagesNames);
                    user.put("aboutDescription",((EditText)findViewById(R.id.description)).getText().toString());

                    db.collection("cities")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Intent intent = new Intent(AddHotelActivity.this, HotelListActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });
                    // Add a new document with a generated ID


                }
        );
        findViewById(R.id.cancel_destination).setOnClickListener(view ->
        {
            Intent intent = new Intent(AddHotelActivity.this, HotelListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}