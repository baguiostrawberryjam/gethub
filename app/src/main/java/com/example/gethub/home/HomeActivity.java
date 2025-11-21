// File: com.example.gethub.home.HomeActivity.java
package com.example.gethub.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.gethub.R;
import com.example.gethub.appointments.AppointmentsFragment;
import com.example.gethub.databinding.ActivityHomeBinding;
import com.example.gethub.models.User;
import com.example.gethub.requests.RequestActivity;
import com.example.gethub.requests.RequestsFragment;

public class HomeActivity extends AppCompatActivity {

    public static final String EXTRA_USER = "extra_user_data";
    public static final String EXTRA_NAVIGATE_TO_DASHBOARD = "extra_navigate_to_dashboard";
    private HomeViewModel viewModel;
    public ActivityHomeBinding binding;
    public String loggedInStudentId;
    public User loggedInUser;

    // Fragments for Navigation
    final Fragment dashboardFragment = new DashboardFragment();
    final Fragment requestsFragment = new RequestsFragment();
    final Fragment appointmentsFragment = new AppointmentsFragment();
    final Fragment searchFragment = new SearchFragment();
    final FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment activeFragment = dashboardFragment;

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

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. Get User Data and Student ID
        if (getIntent().hasExtra(EXTRA_USER)) {
            loggedInUser = getIntent().getParcelableExtra(EXTRA_USER);
            if (loggedInUser != null) {
                loggedInStudentId = loggedInUser.getStudentId();
            }
        }

        if (loggedInStudentId == null) {
            Toast.makeText(this, "Error: Student ID not found.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 2. Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // 3. Setup Fragment Management
        fragmentManager.beginTransaction().add(binding.flContainer.getId(), searchFragment, "4").hide(searchFragment).commit();
        fragmentManager.beginTransaction().add(binding.flContainer.getId(), appointmentsFragment, "3").hide(appointmentsFragment).commit();
        fragmentManager.beginTransaction().add(binding.flContainer.getId(), requestsFragment, "2").hide(requestsFragment).commit();
        fragmentManager.beginTransaction().add(binding.flContainer.getId(), dashboardFragment, "1").commit();

        // 4. Setup Bottom Navigation Listener
        binding.bnvMain.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                swapFragment(dashboardFragment);
                return true;
            } else if (itemId == R.id.nav_requests) {
                swapFragment(requestsFragment);
                return true;
            } else if (itemId == R.id.nav_appointments) {
                swapFragment(appointmentsFragment);
                return true;
            } else if (itemId == R.id.nav_search) {
                Toast.makeText(this, "Search function is currently under construction.", Toast.LENGTH_SHORT).show();
                swapFragment(searchFragment);
                return true;
            } else if (itemId == R.id.nav_new_request) {
                Intent intent = new Intent(HomeActivity.this, RequestActivity.class);
                intent.putExtra(RequestActivity.EXTRA_STUDENT_ID, loggedInStudentId);
                startActivity(intent);
                return false;
            }
            return false;
        });

        // Notification permission logic removed (Handled in LoginActivity)
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (loggedInStudentId != null) {
            viewModel.loadDashboardData(loggedInStudentId);
        }

        // FIX: Check for the flag to force selection of the Home tab
        if (getIntent().getBooleanExtra(EXTRA_NAVIGATE_TO_DASHBOARD, false)) {
            // Programmatically select the Home tab on the Bottom Navigation View
            binding.bnvMain.setSelectedItemId(R.id.nav_home);
            // IMPORTANT: Clear the flag after use so subsequent resumes don't force home
            getIntent().removeExtra(EXTRA_NAVIGATE_TO_DASHBOARD);
        }
    }

    /**
     * Helper function to hide the current fragment and show the new one.
     */
    private void swapFragment(Fragment nextFragment) {
        if (activeFragment != nextFragment) {
            fragmentManager.beginTransaction().hide(activeFragment).show(nextFragment).commit();
            activeFragment = nextFragment;
        }
    }
}