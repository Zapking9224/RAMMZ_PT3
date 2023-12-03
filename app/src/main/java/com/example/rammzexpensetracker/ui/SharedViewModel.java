package com.example.rammzexpensetracker.ui;

import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
