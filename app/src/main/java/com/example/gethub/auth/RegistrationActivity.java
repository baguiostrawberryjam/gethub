package com.example.gethub.auth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import androidx.activity.OnBackPressedCallback; // Import the necessary class

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.gethub.R;
import com.example.gethub.databinding.ActivityRegistrationBinding;

import java.util.Arrays;
import java.util.List;

/**
 * Hosts the multi-page registration flow using ViewPager2.
 */
public class RegistrationActivity extends AppCompatActivity {

    private ActivityRegistrationBinding binding;
    private RegistrationViewModel registrationViewModel;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        registrationViewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);
        viewPager = binding.vpRegistration;

        setupViewPager();
        setupNavigationButtons();
        observeNavigation();
        setupOnBackPressedDispatcher(); // New method to handle back navigation
    }

    private void setupViewPager() {
        RegistrationPagerAdapter pagerAdapter = new RegistrationPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(pagerAdapter);
        // Disable swiping between pages, forcing navigation via buttons
        viewPager.setUserInputEnabled(false);
    }

    private void setupNavigationButtons() {
        // Next Button Click
        binding.btnNext.setOnClickListener(v -> {
            int currentPage = viewPager.getCurrentItem();
            registrationViewModel.goToNextPage(currentPage);
        });

        // Back Button Click
        binding.btnBack.setOnClickListener(v -> {
            int currentPage = viewPager.getCurrentItem();
            registrationViewModel.goToPreviousPage(currentPage);
        });

        // Hide/Show Back Button based on current page
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Hide back button on first page (index 0)
                binding.btnBack.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);

                // Change Next button text to "Register" on the last page (index 4)
                if (position == 4) {
                    binding.btnNext.setText(getString(R.string.btn_register_text));
                    // Assuming R.drawable.bg_gradient_button exists and is the final style
                    // binding.btnNext.setBackgroundResource(R.drawable.bg_gradient_button);
                } else {
                    binding.btnNext.setText(getString(R.string.btn_next_text));
                    // Ensure the button is enabled/disabled by the fragment's validation LiveData
                }

                // Manually trigger the current page's validity to update the Next button status immediately
                updateNextButtonState(position);
            }
        });

        // Initialize button text (since ViewPager starts at page 0)
        binding.btnBack.setVisibility(View.INVISIBLE);
        binding.btnNext.setText(getString(R.string.btn_next_text));
    }

    private void updateNextButtonState(int currentItem) {
        // List of all page validity LiveData objects
        List<LiveData<Boolean>> validationLiveDatas = Arrays.asList(
                registrationViewModel.getPage1Valid(),
                registrationViewModel.getPage2Valid(),
                registrationViewModel.getPage3Valid(),
                registrationViewModel.getPage4Valid(),
                registrationViewModel.getPage5Valid()
        );

        if (currentItem >= 0 && currentItem < validationLiveDatas.size()) {
            LiveData<Boolean> currentValidState = validationLiveDatas.get(currentItem);
            Boolean isValid = currentValidState.getValue();
            // Set the button state based on the current page's latest validation status
            binding.btnNext.setEnabled(isValid != null && isValid);
        }
    }


    private void observeNavigation() {
        registrationViewModel.getNavigateToPage().observe(this, pageIndex -> {
            if (pageIndex != null) {
                viewPager.setCurrentItem(pageIndex, true); // Smooth scroll
                registrationViewModel.onNavigationComplete(); // Clear navigation flag
            }
        });

        // --- General Observer for Next Button Enabling ---
        // Create a list of LiveData for all page validity checks
        List<LiveData<Boolean>> pageValidationLiveDatas = Arrays.asList(
                registrationViewModel.getPage1Valid(),
                registrationViewModel.getPage2Valid(),
                registrationViewModel.getPage3Valid(),
                registrationViewModel.getPage4Valid(),
                registrationViewModel.getPage5Valid()
        );

        // General observer to enable/disable the Next button based on the current page's validity
        for (int i = 0; i < pageValidationLiveDatas.size(); i++) {
            final int pageIndex = i;
            pageValidationLiveDatas.get(i).observe(this, isValid -> {
                if (viewPager.getCurrentItem() == pageIndex) {
                    binding.btnNext.setEnabled(isValid);
                }
            });
        }

        // --- Page 2 OTP Dialog Observer ---
        registrationViewModel.getShowOtpDialogEvent().observe(this, otpCode -> {
            if (otpCode != null) {
                // Show the AlertDialog with the generated code
                showOtpCodeDialog(otpCode);
                // Consume the event immediately
                registrationViewModel.onOtpDialogEventConsumed();
            }
        });
    }

    /**
     * Fakes the OTP sending process by showing the generated code in an AlertDialog.
     * @param otpCode The 6-digit code generated by the ViewModel.
     */
    private void showOtpCodeDialog(String otpCode) {
        // Use a standard AlertDialog as requested
        new AlertDialog.Builder(this)
                .setTitle("OTP Sent (Simulation)")
                .setMessage("An OTP has been sent to your email (for simulation purposes, here is the code): \n\n" + otpCode)
                .setPositiveButton("OK", (dialog, which) -> {
                    // User acknowledges the OTP
                    dialog.dismiss();
                    Toast.makeText(this, "OTP copied! Please enter it below.", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    /**
     * Replaces the deprecated onBackPressed() with an OnBackPressedCallback.
     * Handles navigating back a page, or closing the activity on the first page.
     */
    private void setupOnBackPressedDispatcher() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if (viewPager.getCurrentItem() == 0) {
                    // On the first page, let the system handle the back press (close activity)
                    // We call finish() here instead of super.onBackPressed() because we manually
                    // handle the logic and still want to respect the 'enabled' state of the callback.
                    finish();
                    // Add reverse transition when backing out of Registration
                    // The R.anim.slide_in_left and R.anim.slide_out_right require animation files to exist.
                    // overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                } else {
                    // Navigate to the previous page in the ViewPager
                    registrationViewModel.goToPreviousPage(viewPager.getCurrentItem());
                }
            }
        };
        // Register the callback with the OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}