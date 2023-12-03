package com.example.rammzexpensetracker.ui.settings;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.rammzexpensetracker.MainActivity;
import com.example.rammzexpensetracker.databinding.ActivitySettingsBinding;
import com.example.rammzexpensetracker.ui.SharedViewModel;
import com.example.rammzexpensetracker.ui.User;
import com.example.rammzexpensetracker.ui.dashboard.DashboardFragment;
import com.example.rammzexpensetracker.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // initialize widgets
        final EditText firstNameEditText = binding.name;
        final Button applyChangesButton = binding.applyChangesButton;
        final Button cancelChangesButton = binding.CancelButton;
        final Button signOutButton = binding.signOut;
        final Button deleteAccount = binding.deleteAccount;


        String userId = getIntent().getStringExtra("USER_ID");
        if (userId != null) {
            DatabaseReference userRef = database.getReference().child("users").child(userId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        // displays user's current name in the text input hint
                        firstNameEditText.setHint("Current Name: " + user.getName());
                        System.out.println("User is not null");
                    } else {
                        System.out.println("User is null");
                    }
                    applyChangesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            assert user != null;
                            user.setName(Objects.requireNonNull(firstNameEditText.getText()).toString().trim());
                            if (!firstNameEditText.getText().toString().isEmpty() || !firstNameEditText.getText().toString().equals(user.getName())) {
                                userRef.child("name").setValue(user.getName());
                                firstNameEditText.setHint("Current Name: " + user.getName());
                                // Inform the user that the name has been updated (optional)
                                Toast.makeText(SettingsActivity.this, "Name updated successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SettingsActivity.this, "Nothing to change", Toast.LENGTH_SHORT).show();
                            }
                            firstNameEditText.setText("");
                        }
                    });

                    cancelChangesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                            intent.putExtra("USER_ID", userId);
                            startActivity(intent);
                        }
                    });

                    signOutButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });

                    deleteAccount.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            userRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                    assert firebaseUser != null;
                                    firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            // Account successfully deleted, now navigate to LoginActivity
                                            if (task.isSuccessful()) {
                                                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                            } else {
                                                // Handle the failure to delete the account
                                                Toast.makeText(SettingsActivity.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }
                            });
                        }
                    });
                }



                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("Error: " + error.getMessage());
                }
            });


        } else {
            System.out.println("Lolz");
        }


    }

}
