// File: com.example.gethub.notifications.NotificationActivity.java (FIXED & FUNCTIONAL)
package com.example.gethub.notifications;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.gethub.R;
import com.example.gethub.databinding.ActivityNotificationBinding;
import com.example.gethub.profile.ProfileActivity;

public class NotificationActivity extends AppCompatActivity {

    public static final String EXTRA_USER_ID = "extra_user_id"; // Key for passing Student ID
    private ActivityNotificationBinding binding;
    private NotificationViewModel viewModel;
    private NotificationAdapter adapter;
    private String loggedInStudentId = "1234567890"; // Fallback to mock user ID

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


        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. Get User ID (Assume HomeActivity passed the logged-in ID)
        String userId = getIntent().getStringExtra(EXTRA_USER_ID);
        if (userId != null) {
            loggedInStudentId = userId;
        }

        // 2. Initialize ViewModel and Load Data
        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        viewModel.loadNotifications(loggedInStudentId);

        // 3. Setup RecyclerView and Adapter
        adapter = new NotificationAdapter(viewModel.getNotifications().getValue());
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        binding.rvNotifications.setAdapter(adapter);

        setupHeaderButtons();
        observeViewModel();
    }

    private void setupHeaderButtons() {
        binding.ibBack.setOnClickListener(v -> finish());

        binding.ibProfile.setOnClickListener(v -> {
            // Need the logged-in User Object, which is not passed here.
            // In a complete system, we would fetch the user object or pass the ID to ProfileActivity.
            Toast.makeText(this, "Redirecting to Profile (Missing User Data)", Toast.LENGTH_SHORT).show();
        });
    }

    private void observeViewModel() {
        viewModel.getNotifications().observe(this, notifications -> {
            if (notifications == null || notifications.isEmpty()) {
                binding.tvEmptyState.setVisibility(View.VISIBLE);
                binding.rvNotifications.setVisibility(View.GONE);
            } else {
                adapter.updateList(notifications);
                binding.tvEmptyState.setVisibility(View.GONE);
                binding.rvNotifications.setVisibility(View.VISIBLE);
            }
        });
    }
}