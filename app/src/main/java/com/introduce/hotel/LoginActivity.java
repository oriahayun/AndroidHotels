package com.introduce.hotel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.auth.User;


public class LoginActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button buttonRegister = findViewById(R.id.registerButton);
        Button buttonLogin = findViewById(R.id.loginButton);
        buttonRegister.setOnClickListener(v->{
            startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
        });
        buttonLogin.setOnClickListener(v->{
            String email = ((EditText)findViewById(R.id.username)).getText().toString();
            String password = ((EditText)findViewById(R.id.password)).getText().toString();
            if(!((email.equals("")) || (password.equals("")))){

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this,"Login Ok",Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(LoginActivity.this,HotelListActivity.class));
                                } else {

                                    Toast.makeText(LoginActivity.this,"Login Failed",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }else{
                Toast.makeText(this,"you did not enter you info",Toast.LENGTH_LONG).show();
            }

        });


    }



}