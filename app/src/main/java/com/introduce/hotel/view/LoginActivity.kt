package com.introduce.hotel.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.introduce.hotel.R

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        val currentUser: FirebaseUser? = mAuth.currentUser
        if (currentUser != null) {
            navigateToHotelList()
        }

        val buttonRegister: Button = findViewById(R.id.registerButton)
        val buttonLogin: Button = findViewById(R.id.loginButton)
        buttonRegister.setOnClickListener {
            navigateToRegister()
        }
        buttonLogin.setOnClickListener {
            val email: String = findViewById<EditText>(R.id.username).text.toString()
            val password: String = findViewById<EditText>(R.id.password).text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_LONG).show()
                                navigateToHotelList()
                            } else {
                                Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_LONG).show()
                            }
                        }
            } else {
                Toast.makeText(this@LoginActivity, "You did not enter your info", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToRegister() {
        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToHotelList() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
