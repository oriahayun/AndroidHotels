package com.introduce.hotel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegisterActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_register);
        findViewById(R.id.registerCancel).setOnClickListener(v->{
            finish();
        });
        findViewById(R.id.finish_register).setOnClickListener(v->{
            String id = ((EditText)findViewById(R.id.registerID)).getText().toString();
            String email = ((EditText)findViewById(R.id.registerEmailAddress)).getText().toString();
            String password = ((EditText)findViewById(R.id.registerPassword)).getText().toString();
            Log.d("result" , email +" " + password);

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this,"register ok!",Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(new Intent(RegisterActivity.this,HotelListActivity.class));
                            } else {
                                Toast.makeText(RegisterActivity.this,"register faild!",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        });
    }

}