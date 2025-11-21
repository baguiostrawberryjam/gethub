package com.example.gethub.auth;

import android.Manifest; // Import Manifest
import android.content.Intent;
import android.content.pm.PackageManager; // Import PackageManager
import android.os.Build; // Import Build for version check
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher; // Import Launcher
import androidx.activity.result.contract.ActivityResultContracts; // Import Contracts
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.gethub.databinding.ActivityLoginBinding;
import com.example.gethub.home.HomeActivity;
import com.example.gethub.models.User;

import java.util.ArrayList; // Import ArrayList
import java.util.List; // Import List
import java.util.Map; // Import Map

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;

    // 1. Define the Permission Launcher
    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean cameraGranted = result.getOrDefault(Manifest.permission.CAMERA, false);
                Boolean notificationsGranted = result.getOrDefault(Manifest.permission.POST_NOTIFICATIONS, false);

                if (cameraGranted != null && cameraGranted) {
                    // Optional: Logic if camera is granted
                } else {
                    // Optional: Logic if camera is denied (e.g., show a message saying feature won't work)
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

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

        // 2. Call the permission check when the activity starts
        checkAndRequestPermissions();
    }

    // 3. Implement the Permission Logic
    private void checkAndRequestPermissions() {
        List<String> permissionsToRequest = new ArrayList<>();

        // Check Camera Permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.CAMERA);
        }

        // Check Notification Permission (Only for Android 13 / API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        // If there are permissions to request, launch the dialog
        if (!permissionsToRequest.isEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toArray(new String[0]));
        }
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
                User loggedInUser = ((LoginViewModel.LoginState.Success) loginState).getUser();
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra(HomeActivity.EXTRA_USER, loggedInUser);
                startActivity(intent);
                finish();
            } else if (loginState instanceof LoginViewModel.LoginState.Error) {
                binding.errLogin.setText(((LoginViewModel.LoginState.Error) loginState).getMessage());
                binding.errLogin.setVisibility(View.VISIBLE);
            } else {
                binding.errLogin.setVisibility(View.GONE);
            }
        });
    }
}