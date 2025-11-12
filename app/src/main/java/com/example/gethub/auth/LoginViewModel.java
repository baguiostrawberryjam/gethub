package com.example.gethub.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    // --- Data Fields ---
    private final MutableLiveData<String> username = new MutableLiveData<>("");
    private final MutableLiveData<String> password = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> isLoginButtonEnabled = new MutableLiveData<>(false);
    private final MutableLiveData<LoginState> loginState = new MutableLiveData<>(new LoginState.Idle());

    // --- Login State sealed class/interface to represent outcomes ---
    public interface LoginState {
        class Idle implements LoginState {}
        class Success implements LoginState {}
        class Error implements LoginState {
            private final String message;
            public Error(String message) { this.message = message; }
            public String getMessage() { return message; }
        }
    }

    // --- Public LiveData Accessors ---
    public LiveData<Boolean> getIsLoginButtonEnabled() { return isLoginButtonEnabled; }
    public LiveData<LoginState> getLoginState() { return loginState; }

    // --- Event Handlers ---
    public void onUsernameChanged(String user) {
        username.setValue(user);
        validateInputs();
    }

    public void onPasswordChanged(String pass) {
        password.setValue(pass);
        validateInputs();
    }

    // --- Validation Logic ---
    private void validateInputs() {
        String user = username.getValue();
        String pass = password.getValue();
        boolean isValid = true;

        if (user == null || user.trim().isEmpty()) {
            isValid = false;
        } else if (pass == null || pass.length() < 4) { // Password must be at least 4 chars for the button to enable
            isValid = false;
        }

        isLoginButtonEnabled.setValue(isValid);
        // Reset state to Idle when user types
        loginState.setValue(new LoginState.Idle());
    }

    // --- Login Logic ---
    public void attemptLogin() {
        loginState.setValue(new LoginState.Idle()); // Clear previous state

        String user = username.getValue();
        String pass = password.getValue();

        // Check if inputs are valid before attempting
        if (user == null || user.isEmpty() || pass == null || pass.length() < 4) {
            loginState.setValue(new LoginState.Error("Please enter a valid Student ID and Password."));
            return;
        }

        // Example simulated login check (This is where real authentication logic would go)
        if (user.equals("admin") && pass.equals("1234")) {
            // Simulation of successful authentication
            loginState.setValue(new LoginState.Success());
        } else {
            // Simulation of failed authentication
            loginState.setValue(new LoginState.Error("Invalid Student ID or Password."));
        }
    }
}