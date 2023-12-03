package com.example.rammzexpensetracker.ui.signup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.rammzexpensetracker.R;
import com.example.rammzexpensetracker.databinding.ActivityLoginBinding;
import com.example.rammzexpensetracker.databinding.ActivitySignupBinding;

import com.example.rammzexpensetracker.ui.User;
import com.example.rammzexpensetracker.ui.login.LoginActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private SignupViewModel signupViewModel;
    private ActivitySignupBinding binding;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseApp.initializeApp(this);

        signupViewModel = new ViewModelProvider(this).get(SignupViewModel.class);

        final EditText firstNameEditText = binding.firstName;
        final EditText emailEditText = binding.email;
        final EditText passwordEditText = binding.password;
        final EditText confirmPasswordEditText = binding.confirmPassword;
        final Button signupButton = binding.signUpButton;
        final Button cancelSignupButton = binding.cancelSignupButton;

        cancelSignupButton.setOnClickListener(view -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        });

        signupButton.setOnClickListener(view -> {

            String firstName = firstNameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            FirebaseAuth.getInstance().signOut();


            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (confirmPassword.equals(password)) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                assert user != null;
                                String userId = user.getUid();


                                User newUser = new User(firstName, email);

                                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                                usersRef.child(userId).setValue(newUser);
                                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                // Navigate to the SignUpActivity when the button is clicked
                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                            } else {
                                Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
