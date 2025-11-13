package com.example.gethub.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.gethub.MainActivity;
import com.example.gethub.databinding.ActivityLoginBinding;
import com.example.gethub.models.User;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Retrieve user data from RegistrationActivity and add to the list
        if (getIntent().hasExtra("USER_DATA")) {
            User registeredUser = getIntent().getParcelableExtra("USER_DATA");
            loginViewModel.addRegisteredUser(registeredUser);
        }

        setupInputListeners();
        setupClickListeners();
        observeViewModel();
    }

    private void setupInputListeners() {
        binding.etStudentID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loginViewModel.onStudentIdChanged(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loginViewModel.onPasswordChanged(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupClickListeners() {
        binding.btnLogin.setOnClickListener(v -> loginViewModel.attemptLogin());

        binding.tvRegisterLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });
    }

    private void observeViewModel() {
        loginViewModel.getIsLoginButtonEnabled().observe(this, isEnabled -> {
            binding.btnLogin.setEnabled(isEnabled);
        });

        loginViewModel.getLoginState().observe(this, loginState -> {
            if (loginState instanceof LoginViewModel.LoginState.Success) {
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Prevent returning to the login screen
            } else if (loginState instanceof LoginViewModel.LoginState.Error) {
                binding.errLogin.setText(((LoginViewModel.LoginState.Error) loginState).getMessage());
                binding.errLogin.setVisibility(View.VISIBLE);
            } else {
                binding.errLogin.setVisibility(View.GONE);
            }
        });
    }
}
