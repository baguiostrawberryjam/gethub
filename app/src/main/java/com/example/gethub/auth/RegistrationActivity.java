package com.example.gethub.auth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import androidx.activity.OnBackPressedCallback;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.gethub.R;
import com.example.gethub.databinding.ActivityRegistrationBinding;
import com.example.gethub.models.User;

import java.util.Arrays;
import java.util.List;

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
        setupOnBackPressedDispatcher();
    }

    private void setupViewPager() {
        RegistrationPagerAdapter pagerAdapter = new RegistrationPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setUserInputEnabled(false);
    }
    public ViewPager2 getViewPager() {
        return viewPager;
    }

    private void setupNavigationButtons() {
        // --- Next Button Logic (STAYS IN ACTIVITY) ---
        binding.btnNext.setOnClickListener(v -> {
            int currentPage = viewPager.getCurrentItem();
            registrationViewModel.goToNextPage(currentPage);
        });

        // --- Back Button Listener REMOVED HERE ---
        // The Fragments will now handle the click action for btnBack.

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (position == 4) {
                    binding.btnNext.setText(getString(R.string.btn_register_text));
                } else {
                    binding.btnNext.setText(getString(R.string.btn_next_text));
                }
                updateNextButtonState(position);
            }
        });

        // NOTE: The visibility initial state MUST REMAIN IN THE ACTIVITY
        binding.btnNext.setText(getString(R.string.btn_next_text));
    }

    private void updateNextButtonState(int currentItem) {
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
            binding.btnNext.setEnabled(isValid != null && isValid);
        }
    }

    private void observeNavigation() {
        registrationViewModel.getNavigateToPage().observe(this, pageIndex -> {
            if (pageIndex != null) {
                viewPager.setCurrentItem(pageIndex, true);
                registrationViewModel.onNavigationComplete();
            }
        });

        List<LiveData<Boolean>> pageValidationLiveDatas = Arrays.asList(
                registrationViewModel.getPage1Valid(),
                registrationViewModel.getPage2Valid(),
                registrationViewModel.getPage3Valid(),
                registrationViewModel.getPage4Valid(),
                registrationViewModel.getPage5Valid()
        );

        for (int i = 0; i < pageValidationLiveDatas.size(); i++) {
            final int pageIndex = i;
            pageValidationLiveDatas.get(i).observe(this, isValid -> {
                if (viewPager.getCurrentItem() == pageIndex) {
                    binding.btnNext.setEnabled(isValid);
                }
            });
        }

        registrationViewModel.getShowOtpDialogEvent().observe(this, otpCode -> {
            if (otpCode != null) {
                showOtpCodeDialog(otpCode);
                registrationViewModel.onOtpDialogEventConsumed();
            }
        });

        registrationViewModel.getRegistrationCompleteEvent().observe(this, user -> {
            if (user != null) {
                // This is where you would typically save the user to a database
                // For now, we'll just show a toast and navigate to the login screen
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                intent.putExtra("USER_DATA", user); // Pass the new user data
                startActivity(intent);
                finish(); // Close the registration activity
                registrationViewModel.onRegistrationComplete();
            }
        });
    }

    private void showOtpCodeDialog(String otpCode) {
        new AlertDialog.Builder(this)
                .setTitle("OTP Sent (Simulation)")
                .setMessage("An OTP has been sent to your email (for simulation purposes, here is the code): \n\n" + otpCode)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    Toast.makeText(this, "OTP copied! Please enter it below.", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void setupOnBackPressedDispatcher() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (viewPager.getCurrentItem() == 0) {
                    finish();
                } else {
                    registrationViewModel.goToPreviousPage(viewPager.getCurrentItem());
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}
