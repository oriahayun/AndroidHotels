package com.introduce.hotel.view;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

import com.introduce.hotel.R;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 71;
    private ImageView imgProfile;
    private Uri filePath;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private EditText edtName;
    private TextView txtName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        imgProfile = view.findViewById(R.id.profile_image);
        Button btnChooseImage = view.findViewById(R.id.edit_profile_image_button);
        Button btnUpdate = view.findViewById(R.id.edit_profile_name_button);
        edtName = view.findViewById(R.id.profile_name_edittext); // EditText for name input
        txtName = view.findViewById(R.id.profile_name); // TextView for displaying name

        // Initialize with current user info
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (user.getPhotoUrl() != null) {
                imgProfile.setImageURI(user.getPhotoUrl()); // Set the user's profile image if exists
            }
            txtName.setText(user.getDisplayName()); // Set the user's name
        }

        btnChooseImage.setOnClickListener(v -> chooseImage());

        btnUpdate.setOnClickListener(v -> updateProfile());

        return view;
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void updateProfile() {
        String name = edtName.getText().toString().trim();
        if (filePath != null) {
            StorageReference ref = storageReference.child("profileImages/" + mAuth.getCurrentUser().getUid());
            ref.putFile(filePath).addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .setPhotoUri(uri)
                        .build();
                mAuth.getCurrentUser().updateProfile(profileUpdate)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                                txtName.setText(name); // Update displayed name
                            }
                        });
            })).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show());
        } else {
            // Update name only
            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();
            mAuth.getCurrentUser().updateProfile(profileUpdate)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                            txtName.setText(name); // Update displayed name
                        }
                    });
        }
    }

    @Override
    public  void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                imgProfile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
