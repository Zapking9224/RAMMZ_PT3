package com.example.rammzexpensetracker.ui.ForgotPassword;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.rammzexpensetracker.databinding.ActivityForgotPasswordBinding;
import com.example.rammzexpensetracker.databinding.ActivitySignupBinding;
import com.example.rammzexpensetracker.ui.login.LoginActivity;
import com.example.rammzexpensetracker.ui.signup.SignUpActivity;
import com.example.rammzexpensetracker.ui.signup.SignupViewModel;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseAuth auth = FirebaseAuth.getInstance();


        final EditText emailEditText = binding.emailTextBox;
        final Button submitButton = binding.submitButton;
        final Button cancelButton = binding.cancelButton;

        cancelButton.setOnClickListener(view -> {
            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
        });

        submitButton.setOnClickListener(view -> {
            if (TextUtils.isEmpty(emailEditText.getText())) {
                emailEditText.setError("Email cannot be empty");
            } else if (!Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText()).matches()) {
                emailEditText.setError("Enter a valid email address");
            } else {
                String email = emailEditText.getText().toString().trim();
                // gets reference to users in the database.
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

                usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            sendPasswordResetEmail(email);
                        } else {
                            // If the user does not exist
                            Toast.makeText(ForgotPasswordActivity.this, "User with this email does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ForgotPasswordActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // Function to send a password reset email
    private void sendPasswordResetEmail(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                    } else {
                        // If the user does not exist or other errors occurred
                        Toast.makeText(ForgotPasswordActivity.this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}