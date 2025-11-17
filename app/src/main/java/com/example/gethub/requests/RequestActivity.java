// File: com.example.gethub.requests.RequestActivity.java (FINAL FIX FOR NAVIGATION)
package com.example.gethub.requests;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.gethub.R;
import com.example.gethub.databinding.ActivityRequestBinding;
import com.example.gethub.home.HomeActivity;
import com.example.gethub.models.RequestTicket;
import com.example.gethub.models.User;
import com.example.gethub.notifications.NotificationHelper;

import java.util.Arrays;
import java.util.List;

public class RequestActivity extends AppCompatActivity {

    public static final String EXTRA_STUDENT_ID = "extra_student_id";
    private ActivityRequestBinding binding;
    private RequestViewModel requestViewModel;
    private ViewPager2 viewPager;

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


        binding = ActivityRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        requestViewModel = new ViewModelProvider(this).get(RequestViewModel.class);
        viewPager = binding.vpRequest;

        // Initialize ViewModel with Student ID passed from HomeActivity
        String studentId = getIntent().getStringExtra(EXTRA_STUDENT_ID);
        if (studentId == null) {
            Toast.makeText(this, "Error: Student ID not passed for request.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        requestViewModel.init(studentId);

        setupViewPager();
        setupNavigationButtons();
        observeNavigation();
    }

    private void setupViewPager() {
        RequestPagerAdapter pagerAdapter = new RequestPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setUserInputEnabled(false);
    }

    private void setupNavigationButtons() {
        // --- Next Button Logic (STAYS IN ACTIVITY) ---
        binding.btnNext.setOnClickListener(v -> {
            int currentPage = viewPager.getCurrentItem();
            requestViewModel.goToNextPage(currentPage);
        });

        // --- Back Button Logic REMOVED HERE ---
        // The Fragment will now handle its local btnBack click listener.

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                // Page 2 is the final page (index 1)
                if (position == 1) {
                    binding.btnNext.setText(getString(R.string.btn_submit_request_text));
                } else {
                    binding.btnNext.setText(getString(R.string.btn_next_text));
                }
                // Visibility for btnBack and btnNext state are handled in the Fragments
                updateNextButtonState(position);
            }
        });

        binding.btnNext.setText(getString(R.string.btn_next_text));
    }

    private void updateNextButtonState(int currentItem) {
        List<LiveData<Boolean>> validationLiveDatas = Arrays.asList(
                requestViewModel.getPage1Valid(),
                requestViewModel.getPage2Valid()
        );

        if (currentItem >= 0 && currentItem < validationLiveDatas.size()) {
            LiveData<Boolean> currentValidState = validationLiveDatas.get(currentItem);
            Boolean isValid = currentValidState.getValue();
            binding.btnNext.setEnabled(isValid != null && isValid);
        }
    }

    private void observeNavigation() {

        // FIX: ADDED THE PRIMARY NAVIGATION OBSERVER
        requestViewModel.getNavigateToPage().observe(this, pageIndex -> {
            if (pageIndex != null) {
                viewPager.setCurrentItem(pageIndex, true);
                requestViewModel.onNavigationComplete();
            }
        });

        // Observe Page 1 validation (to enable/disable the Next button on Page 1)
        requestViewModel.getPage1Valid().observe(this, isValid -> {
            if (viewPager.getCurrentItem() == 0) {
                binding.btnNext.setEnabled(isValid);
            }
        });

        // Observe Page 2 validation (to enable/disable the Submit button on Page 2)
        requestViewModel.getPage2Valid().observe(this, isValid -> {
            if (viewPager.getCurrentItem() == 1) {
                binding.btnNext.setEnabled(isValid);
            }
        });

        // Observe Request Completion Event
        requestViewModel.getRequestCompleteEvent().observe(this, finalTicket -> {
            if (finalTicket != null) {
                // Show success dialog (Simulation of Business Rule NRD2)
                new AlertDialog.Builder(this)
                        .setTitle("Request Successful")
                        .setMessage("Your request has been successfully submitted! Tap OK to view your ticket.")
                        .setPositiveButton("OK", (dialog, which) -> {

                            // 🚀 PUSH NOTIFICATION IMPLEMENTATION (MOCK TRIGGER)
                            NotificationHelper.showTicketNotification(this, finalTicket);

                            // Redirect to Ticket Detail Activity
                            Intent intent = new Intent(this, TicketDetailActivity.class);
                            intent.putExtra(TicketDetailActivity.EXTRA_TICKET, finalTicket);
                            startActivity(intent);
                            finish();
                        })
                        .show();
                requestViewModel.onRequestComplete(); // Reset event
            }
        });
    }
}