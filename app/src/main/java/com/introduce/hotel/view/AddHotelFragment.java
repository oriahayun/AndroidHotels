package com.introduce.hotel.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.introduce.hotel.R;

import java.util.HashMap;
import java.util.Map;

public class AddHotelFragment extends Fragment {
    private String selectedImagesNames;
    private Uri selectedImage;
    private ImageView imageViews;

    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 11;

    private void selectImages(int req_code) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), req_code);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_hotel, container, false);

        imageViews = rootView.findViewById(R.id.cityImage);

        imageViews.setOnClickListener(v -> {
            selectImages(0);
        });

        Button addButton = rootView.findViewById(R.id.add_destination);
        addButton.setOnClickListener(
                view -> {
                    selectedImagesNames = ((EditText) rootView.findViewById(R.id.cityName)).getText().toString();

                    if (selectedImagesNames.isEmpty() || selectedImage == null) {
                        Toast.makeText(getContext(), "Enter the content correctly.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    uploadImagesToFirebaseStorage();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    // Create a new user with a first and last name
                    Map<String, Object> user = new HashMap<>();
                    user.put("mainImage", selectedImagesNames + ".jpg");

                    user.put("cityName", selectedImagesNames);
                    user.put("aboutDescription", ((EditText) rootView.findViewById(R.id.description)).getText().toString());

                    db.collection("cities")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    NavHostFragment.findNavController(AddHotelFragment.this)
                                            .navigate(R.id.action_addHotelFragment3_to_hotelListFragment);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });
                }
        );

        rootView.findViewById(R.id.cancel_destination).setOnClickListener(view -> {
            NavHostFragment.findNavController(AddHotelFragment.this)
                    .navigate(R.id.action_addHotelFragment3_to_hotelListFragment);
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK) {
            // Single image selected
            Uri imageUri = data.getData();
            selectedImage = imageUri;
            imageViews.setImageURI(imageUri);
        }
    }

    private void uploadImagesToFirebaseStorage() {
        if (selectedImage != null) {
            String fileName = selectedImagesNames + ".jpg";

            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("travel/" + fileName);
            UploadTask uploadTask = storageRef.putFile(selectedImage);

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), "android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
        }
    }
}
