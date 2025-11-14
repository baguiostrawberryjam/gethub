// File: com.example.gethub.home.HomeActivity.java (REFACTORED WITH BINDING)
package com.example.gethub.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.gethub.R;
import com.example.gethub.appointments.AppointmentsFragment;
import com.example.gethub.databinding.ActivityHomeBinding; // NEW IMPORT
import com.example.gethub.models.User;
import com.example.gethub.requests.RequestActivity;
import com.example.gethub.requests.RequestsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    public static final String EXTRA_USER = "extra_user_data";
    private HomeViewModel viewModel;
    private ActivityHomeBinding binding; // NEW BINDING VARIABLE
    private String loggedInStudentId;

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
        binding = ActivityHomeBinding.inflate(getLayoutInflater()); // INITIALIZE BINDING
        setContentView(binding.getRoot()); // SET CONTENT VIEW VIA BINDING

        // 1. Get User Data and Student ID
        if (getIntent().hasExtra(EXTRA_USER)) {
            User loggedInUser = getIntent().getParcelableExtra(EXTRA_USER);
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

        // 3. Setup Fragment Management (Using binding IDs)
        fragmentManager.beginTransaction().add(binding.flContainer.getId(), searchFragment, "4").hide(searchFragment).commit();
        fragmentManager.beginTransaction().add(binding.flContainer.getId(), appointmentsFragment, "3").hide(appointmentsFragment).commit();
        fragmentManager.beginTransaction().add(binding.flContainer.getId(), requestsFragment, "2").hide(requestsFragment).commit();
        fragmentManager.beginTransaction().add(binding.flContainer.getId(), dashboardFragment, "1").commit();

        // 4. Setup Bottom Navigation Listener (Using binding ID)
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
                // '+' Button: Redirect to Request Document page (Activity)
                Intent intent = new Intent(HomeActivity.this, RequestActivity.class);
                intent.putExtra(RequestActivity.EXTRA_STUDENT_ID, loggedInStudentId);
                startActivity(intent);
                return false;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data every time the activity comes to the foreground
        if (loggedInStudentId != null) {
            viewModel.loadDashboardData(loggedInStudentId);
        }
    }

    /**
     * Helper function to hide the current fragment and show the new one.
     */
    private void swapFragment(Fragment nextFragment) {
        if (activeFragment != nextFragment) {
            // Uses binding ID for the container
            fragmentManager.beginTransaction().hide(activeFragment).show(nextFragment).commit();
            activeFragment = nextFragment;
        }
    }
}