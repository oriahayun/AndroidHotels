package com.introduce.hotel.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.introduce.hotel.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navigateToHotelList();
        }

        Button buttonRegister = findViewById(R.id.registerButton);
        Button buttonLogin = findViewById(R.id.loginButton);
        buttonRegister.setOnClickListener(v -> {
            navigateToRegister();
        });
        buttonLogin.setOnClickListener(v -> {
            String email = ((EditText) findViewById(R.id.username)).getText().toString();
            String password = ((EditText) findViewById(R.id.password)).getText().toString();
            if (!(email.isEmpty() || password.isEmpty())) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                                navigateToHotelList();
                            } else {
                                Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                            }
                        });
            } else {
                Toast.makeText(LoginActivity.this, "You did not enter your info", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void navigateToHotelList() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
