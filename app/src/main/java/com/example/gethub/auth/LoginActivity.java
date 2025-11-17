package com.example.gethub.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.gethub.MainActivity;
import com.example.gethub.R;
import com.example.gethub.databinding.ActivityLoginBinding;
import com.example.gethub.home.HomeActivity;
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
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

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
                // **UPDATED: Get the User object from the Success state**
                User loggedInUser = ((LoginViewModel.LoginState.Success) loginState).getUser();

                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                // **UPDATED: Navigate to HomeActivity and pass data**
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra(HomeActivity.EXTRA_USER, loggedInUser); // Pass the User object
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
