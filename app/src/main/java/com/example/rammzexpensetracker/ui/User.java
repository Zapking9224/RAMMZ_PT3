package com.example.rammzexpensetracker.ui;

import com.example.rammzexpensetracker.ui.dashboard.Budget;
import com.example.rammzexpensetracker.ui.expenses.Expense;

import java.util.ArrayList;

public class User {

    private String name;
    private String email;

    // Required no-argument constructor for Firebase
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
