// File: com.example.gethub.auth.LoginViewModel.java
package com.example.gethub.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.gethub.models.User;
import java.util.ArrayList;
import java.util.List;

public class LoginViewModel extends ViewModel {

    // --- Data Fields ---
    private final MutableLiveData<String> studentId = new MutableLiveData<>("");
    private final MutableLiveData<String> password = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> isLoginButtonEnabled = new MutableLiveData<>(false);
    private final MutableLiveData<LoginState> loginState = new MutableLiveData<>(new LoginState.Idle());
    // Use a static list to hold users for the app's lifecycle, since there's no database.
    private static final List<User> registeredUsers = new ArrayList<>();

    // --- Login State sealed class/interface to represent outcomes ---
    public interface LoginState {
        class Idle implements LoginState {}
        // **UPDATED:** Success now carries the User object
        class Success implements LoginState {
            private final User user;
            public Success(User user) { this.user = user; }
            public User getUser() { return user; }
        }
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
    public void onStudentIdChanged(String id) {
        studentId.setValue(id);
        validateInputs();
    }

    public void onPasswordChanged(String pass) {
        password.setValue(pass);
        validateInputs();
    }

    // Add a new user to our in-memory list
    public void addRegisteredUser(User user) {
        if (user != null) {
            // Avoid adding duplicates by checking studentId
            boolean userExists = false;
            for (User existingUser : registeredUsers) {
                if (existingUser.getStudentId().equals(user.getStudentId())) {
                    userExists = true;
                    break;
                }
            }
            if (!userExists) {
                registeredUsers.add(user);
            }
        }
    }

    // --- Validation Logic ---
    private void validateInputs() {
        String id = studentId.getValue();
        String pass = password.getValue();
        boolean isValid = id != null && !id.trim().isEmpty() && pass != null && !pass.trim().isEmpty();
        isLoginButtonEnabled.setValue(isValid);
        loginState.setValue(new LoginState.Idle());
    }

    // --- Login Logic ---
    public void attemptLogin() {
        loginState.setValue(new LoginState.Idle());

        String id = studentId.getValue();
        String pass = password.getValue();

        if (id == null || id.isEmpty() || pass == null || pass.isEmpty()) {
            loginState.setValue(new LoginState.Error("Please enter a valid Student ID and Password."));
            return;
        }

        // Fallback for default admin login
        if (id.equals("admin") && pass.equals("1234")) {
            // **UPDATED:** Create a dummy user object for admin fallback
            User adminUser = new User();
            adminUser.setFirstName("Admin");
            adminUser.setStudentId("admin");
            loginState.setValue(new LoginState.Success(adminUser));
            return;
        }

        // Check against the list of registered users
        boolean foundUser = false;
        for (User user : registeredUsers) {
            if (id.equals(user.getStudentId()) && pass.equals(user.getPassword())) {
                // **UPDATED:** Pass the found User object to the Success state
                loginState.setValue(new LoginState.Success(user));
                foundUser = true;
                break;
            }
        }

        if (!foundUser) {
            loginState.setValue(new LoginState.Error("Invalid Student ID or Password."));
        }
    }
}