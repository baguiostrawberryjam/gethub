package com.example.gethub.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;
import com.example.gethub.databinding.ActivityLoginBinding;
// import com.example.gethub.HomeActivity; // Placeholder for main app screen

import java.util.function.Consumer;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Uses ViewBinding based on your layout name (ActivityLoginBinding)
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // 1. LiveData Observers
        loginViewModel.getLoginState().observe(this, state -> {
            if (state instanceof LoginViewModel.LoginState.Error) {
                // Show general error message (e.g., Invalid credentials)
                binding.errLogin.setText(((LoginViewModel.LoginState.Error) state).getMessage());
                binding.errLogin.setVisibility(android.view.View.VISIBLE); // Make error visible
            } else if (state instanceof LoginViewModel.LoginState.Success) {
                Toast.makeText(this, "Login Successful! Redirecting...", Toast.LENGTH_SHORT).show();
                // Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                // startActivity(intent);
                // finish();
            } else if (state instanceof LoginViewModel.LoginState.Idle) {
                // Clear general error when returning to Idle
                binding.errLogin.setText("");
                binding.errLogin.setVisibility(android.view.View.GONE); // Hide error
            }
        });

        loginViewModel.getIsLoginButtonEnabled().observe(this, enabled -> {
            binding.btnLogin.setEnabled(enabled);
        });

        // 2. Real-time Input Listeners (etStudentID and etPassword)
        setupTextWatcher(binding.etStudentID, loginViewModel::onUsernameChanged);
        setupTextWatcher(binding.etPassword, loginViewModel::onPasswordChanged);

        // 3. Button and Link Click Handlers

        // Main Login Button
        binding.btnLogin.setOnClickListener(v -> loginViewModel.attemptLogin());

        // Forgot Password Link
        binding.tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Opening Forgot Password Screen...", Toast.LENGTH_SHORT).show();
        });

        // Sign-up Link
        binding.tvRegisterLink.setOnClickListener(v -> {
            Toast.makeText(this, "Opening Registration...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class); // Placeholder
            startActivity(intent);
        });

        // Social Login Buttons
        binding.btnGoogle.setOnClickListener(v -> {
            Toast.makeText(this, "Initiating Google Sign-in...", Toast.LENGTH_SHORT).show();
        });

        binding.btnApple.setOnClickListener(v -> {
            Toast.makeText(this, "Initiating Apple Sign-in...", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Helper method to reduce boilerplate for TextWatchers used for real-time validation.
     * @param editText The EditText view to watch.
     * @param consumer The ViewModel function to call with the new text.
     */
    private void setupTextWatcher(android.widget.EditText editText, Consumer<String> consumer) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                consumer.accept(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }
}