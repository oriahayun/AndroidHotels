package com.introduce.hotel.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.introduce.hotel.R;

public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            NavHostFragment.findNavController(LoginFragment.this)
                    .navigate(R.id.action_loginFragment_to_hotelListFragment);
        }

        Button buttonRegister = rootView.findViewById(R.id.registerButton);
        Button buttonLogin = rootView.findViewById(R.id.loginButton);
        buttonRegister.setOnClickListener(v -> {
            NavHostFragment.findNavController(LoginFragment.this)
                    .navigate(R.id.action_loginFragment_to_registerFragment);
        });
        buttonLogin.setOnClickListener(v -> {
            String email = ((EditText) rootView.findViewById(R.id.username)).getText().toString();
            String password = ((EditText) rootView.findViewById(R.id.password)).getText().toString();
            if (!(email.isEmpty() || password.isEmpty())) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Login Successful", Toast.LENGTH_LONG).show();
                                NavController navController = Navigation.findNavController(rootView);
                                navController.navigate(R.id.action_loginFragment_to_hotelListFragment);
                            } else {
                                Toast.makeText(getContext(), "Login Failed", Toast.LENGTH_LONG).show();
                            }
                        });
            } else {
                Toast.makeText(getContext(), "You did not enter your info", Toast.LENGTH_LONG).show();
            }
        });

        return rootView;
    }


}
