package com.example.rammzexpensetracker.ui.signup;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;

public class SignupViewModel extends ViewModel {
    // MutableLiveData for holding the signup result
    private MutableLiveData<SignUpResult> signUpResult = new MutableLiveData<>();

    // Method to perform signup
    public void signUp(String email, String password) {
        // Perform signup logic here (e.g., using Firebase authentication)

        // For demonstration purposes, let's assume the signup is successful
        boolean isSignUpSuccessful = true;

        if (isSignUpSuccessful) {
            signUpResult.setValue(new SignUpResult.Success());
        } else {
            signUpResult.setValue(new SignUpResult.Error("Signup failed. Please try again."));
        }
    }

    // LiveData to observe the signup result
    public LiveData<SignUpResult> getSignUpResult() {
        return signUpResult;
    }

    // Custom class for representing signup results
    public static class SignUpResult {
        // Subclasses for different signup outcomes
        public static class Success extends SignUpResult {}

        public static class Error extends SignUpResult {
            private String errorMessage;

            public Error(String errorMessage) {
                this.errorMessage = errorMessage;
            }

            public String getErrorMessage() {
                return errorMessage;
            }
        }
    }
}
