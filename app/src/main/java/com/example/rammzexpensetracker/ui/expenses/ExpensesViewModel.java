package com.example.rammzexpensetracker.ui.expenses;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ExpensesViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ExpensesViewModel() {
        mText = new MutableLiveData<>();
        //mText.setValue("This is expenses fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}